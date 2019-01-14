'use strict'
var express = require('express')
var bodyParser = require('body-parser')
const Sequelize = require('sequelize')
const sha256 = require('sha256')
const WebSocket = require('ws')

const Op = Sequelize.Op;

const app = express();

app.use(function(req, res, next) {
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	next();
  });

const sequelize = new Sequelize('quyzygy_db','root','',{
	dialect : 'mysql',
	define : {
		timestamps : false
	}
});

app.use(bodyParser.json()); // Accept  JSON Params
app.use(bodyParser.urlencoded({extended:true})); // Accept URL encoded params


app.use(express.static('../frontend/build'))

//#region Sequelize table definitions

const Users = sequelize.define('Users', {
	firstName :{
	    type : Sequelize.STRING,
	    allowNull : false,
	    validate : { 
	        len : [3, 20]
	    }
	},
	lastName :{
	    type : Sequelize.STRING,
	    allowNull : false,
	    validate : { 
	        len : [3, 20]
	    }
	},
	email :{
	    type : Sequelize.STRING,
	    allowNull : false,
	    validate : { 
	        len : [3, 50]
	    },
	    unique: true
	},
	passwordHash :{
	    type : Sequelize.STRING,
	    allowNull : false,
	    validate : { 
	        len : [0, 64]
	    }
	},
	userType: {
	    type: Sequelize.STRING,
	    allowNull:false
	}
})

const Quizzes = sequelize.define('Quizzes', {
	ID: {
		type: Sequelize.INTEGER,
		allowNull: false
	},
	Author:{
		type: Sequelize.STRING,
		allowNull: false,
		len: [0, 50]
	},
	CourseName:{
		type: Sequelize.STRING,
		allowNull: false,
		len: [0, 50]
	},
	Questions:{
		type: Sequelize.STRING,
		allowNull: false,
		len: [0, 8192]
	},
	QuizName:{
		type:Sequelize.STRING,
		allowNull:false
	},
	Duration:{
		type:Sequelize.INTEGER,
		allowNull:false
	},
	Public:{
		type:Sequelize.BOOLEAN,
		allowNull:false
	},
	Geofencing:{
		type:Sequelize.BOOLEAN,
		allowNull:false
	},
	GeofencingData:{
		type:Sequelize.STRING,
		allowNull:true,
		len:[0,4096]
	}
})

const Questions = sequelize.define('Questions', {
	Author:{
		type: Sequelize.STRING,
		allowNull: false,
		len: [0, 50]
	},
	Text:{
		type:Sequelize.STRING,
		allowNull:false,
		len:[0,2048]
	},
	Type:{
		type:Sequelize.STRING,
		allowNull:false,
		len:[0,512]
	},
	Answers:{
		type:Sequelize.STRING,
		allowNull:true,
		len:[0,4196]
	},
	Points:{
		type:Sequelize.INTEGER,
		allowNull:false
	},
	CorrectAnswer:{
		type:Sequelize.STRING,
		allowNull:false,
		len:[0, 512]
	}
})

const Grades = sequelize.define('Grades', {
	UserID:{
		type:Sequelize.STRING,
		allowNull:false,
		len:[0,50]
	},
	QuizID:{
		type:Sequelize.INTEGER,
		allowNull:false
	},
	Value:{
		type:Sequelize.INTEGER,
		allowNull:false
	},
	Date:{
		type:Sequelize.DATE,
		allowNull:false
	}
})

const AuthenticatedUsers = sequelize.define('AuthenticatedUsers',{
	Email:{
		type:Sequelize.STRING,
		allowNull:false,
		len:[0,50]
	},
	SecretKeys:{
		type:Sequelize.STRING,
		allowNull:true,
		len:[0,4096]
	}
})

const ActivatedQuizzes = sequelize.define('ActivatedQuizzes', {
	QuizID:{
		type:Sequelize.INTEGER,
		allowNull:false
	},
	AccessCode:{
		type:Sequelize.INTEGER,
		allowNull:false
	},
	Students:{
		type:Sequelize.STRING,
		allowNull:true,
		len:[0, 1048576]
	},
	StartTime:{
		type:Sequelize.TIME,
		allowNull:true
	},
	EndTime:{
		type:Sequelize.TIME,
		allowNull:true
	}
})

//#endregion

//#region Database manipulation

//#region Authentication and registration

app.post('/login', async (req, res)=>{
	try {
		var result = await Users.findAll({
			where:{
				email:req.param("email"),
				passwordHash:req.param("passwordHash")
			}
		})
		console.warn("result:"+result)
		if (result.rows == 0){
			res.status(400).json({message:"failure"})
		}
		else{
			var sk = generateRandomSecretKey()
			var sel = await AuthenticatedUsers.findAll({
					where:{
						email:req.param('email')
					}
				}
			)
			var existingSKs = [];
			try{
				existingSKs = JSON.parse(sel[0].SecretKeys)
			}
			catch (e){

			}
			existingSKs.push(sk)
			if (existingSKs.length == 1)
				await AuthenticatedUsers.insertOrUpdate({Email:req.param('email'), SecretKeys:JSON.stringify(existingSKs)})
			else
				await AuthenticatedUsers.update({Email:req.param('email'), SecretKeys:JSON.stringify(existingSKs)},
			{where: { email: req.param('email')}})

			res.status(200).json({secretKey:sk, userType:result[0].userType})
		}
	}
	catch(e){
		console.warn(e)
		res.status(500).json({message : 'server error'})
	}
})

app.get('/checkUser', async (req, res) => {
	try{
		let pageSize = 10
		let params = {
			where : {},
			order : [['lastName', 'ASC'], ['firstName', 'ASC']]
		}
	    if (req.query){
	    	if (req.query.filter){
	    		params.where.lastName = {
                	[Op.like] : `%${req.query.filter}%`
                }
	    	}
	    	if (req.query.pageSize){
	    		pageSize = parseInt(req.query.pageSize)
	    	}
	    	if (req.query.pageNo){
	    		params.offset = parseInt(req.query.pageNo) * pageSize
	    		params.limit = pageSize
	    
	    }
		let users = await Users.findAll(params)
		res.status(200).json(users)
	}
}
	catch(e){
		console.warn(e)
		res.status(500).json({message : 'server error'})
	}
});

app.post('/register', async (req, res) => {
	try{
		//TODO: validate credentials
		await Users.create(req.body)
		res.status(201).json({success : true})
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error : e.message})
	}
})

//#endregion

//#region Quiz manipulation

app.post('/createQuiz', async (req, res) => {
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		await Quizzes.create(req.body)
		res.status(201).json({message : 'created'})
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error : e.message})
	}
})

app.get('/myQuizzes', async (req, res)=>{
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		var sel = await Quizzes.findAll({
			where:{
				Author:await getEmailForLoggedUser(req)
			}
		})
		res.status(200).json(sel)
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error : e.message})
	}
})

var liveQuizzes = {}

app.post('/activateQuiz', async(req, res)=>{
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		var code = 1000 + getRandomInt(8999)
		var quizID = req.param('quizID')
		var qres = await ActivatedQuizzes.findAll({
			where:{
				QuizID : quizID
			}})
		if (qres.length == 0){
			await ActivatedQuizzes.insertOrUpdate({QuizID:quizID,AccessCode:code,Students:"",StartTime:"",EndTime:""})
			liveQuizzes[code] = {Students:{},StartTime:new Date(0,0,0,0,0,0,0),EndTime:new Date(0,0,0,0,0,0,0)}
			res.status(200).json({success:true,"AccessCode":code})
		} else{
			if (!liveQuizzes.hasOwnProperty(qres[0].AccessCode)){
				liveQuizzes[qres[0].AccessCode] = {Students:{},StartTime:new Date(0,0,0,0,0,0,0),EndTime:new Date(0,0,0,0,0,0,0)}
			}
			res.status(200).json({success:true,"AccessCode":qres[0].AccessCode})
		}
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error:e.message})
	}
})

app.post("/startQuiz", async (req, res)=>{
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		var quizID = req.param('quizID')
		liveQuiz_WSS.clients.forEach(function each(client) {
			if (client.readyState === WebSocket.OPEN) {
				client.send(JSON.stringify({Action:"QuizStarted", Data:[]}))
			}
		})
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error:e.message})
	}
})

//#endregion

//#region Question manipulation

app.post('/createQuestion', async (req, res) => {
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		var id = -1
		await Questions.create(req.body).then(function(x){id=x.id})
		res.status(201).json({Created : id})
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error : e.message})
	}
})

app.get('/myQuestions', async (req, res)=>{
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		var sel = await Questions.findAll({
			where:{
				Author:await getEmailForLoggedUser(req)
			}
		})
		res.status(200).json(sel)
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error : e.message})
	}
})

//#endregion

//#region Grade manipulation

app.get('/myGrades', async(req, res) =>{
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		var email = await getEmailForLoggedUser(req)
		var grades = await Grades.findAll({
			where:{
				userID:email
			}
		})
		res.status(200).json(grades)
	}
	catch(e){
		console.warn(e)
		res.status(500).json({error : e.message})
	}
})

//#endregion

//#endregion

//#region Methods

function getRandomInt(max) {
	return Math.floor(Math.random() * Math.floor(max));
}

function generateRandomSecretKey(){
	var seed = "abcdef0123456789"
	var sk = ""
	for(var i = 0; i < 32; i++){
		sk += seed[getRandomInt(seed.length)]
	}
	return sk
}

async function validateUser(request){
	return (await checkSecretKey(request.param('sk')))
}
async function isAuthenticated(wsClientPacket){
	return (await checkSecretKey(wsClientPacket.Identity.SecretKey))
}

async function getEmailForLoggedUser(request){
	try {
		var sel = await AuthenticatedUsers.findAll()
			for (var j = 0; j < sel.length; j++){
			var existingSKs = [];
			try{
				existingSKs = JSON.parse(sel[j].SecretKeys)
			}
			catch (e){

			}
			for (var i = 0; i < existingSKs.length; i++)
				if (existingSKs[i] == request.param('sk')){
					return sel[j].Email
				}
			}
	}
	catch(e){
		console.warn(e)
	}
	return ""
}

async function checkSecretKey(sk){
	try {
		var sel = await AuthenticatedUsers.findAll()
			for (var j = 0; j < sel.length; j++){
			var existingSKs = [];
			try{
				existingSKs = JSON.parse(sel[j].SecretKeys)
			}
			catch (e){

			}
			for (var i = 0; i < existingSKs.length; i++)
				if (existingSKs[i] == sk)
					return true
			}
	}
	catch(e){
		console.warn(e)
	}
	return false
}

app.get('/test', async (req, res)=>{
	try{
		var result = await getEmailForLoggedUser(req)
		res.status(200).json({success:result})
	}
	catch(e){
		console.warn(e)
		res.status(500).status({error:e})
	}
})

//#endregion

var cors = require('cors')
app.use(cors())
app.options('*', cors())
app.use(express.static("/"));

app.listen(8080)

// #region WebSocket server

const quizStatus_WSS = new WebSocket.Server({
	port: 8081
})

quizStatus_WSS.on('connection', function connection(ws){
	ws.on('message', function incoming(message){
		console.log('quizStatus_WSS received: %s', message)
	})
	ws.send(JSON.stringify({Success:true,Data:"Connected"}))
})

const liveQuiz_WSS = new WebSocket.Server({
	port:8082
})

liveQuiz_WSS.on('connection', function connection(ws){
	ws.id = quizStatus_WSS.getUniqueID()
	ws.on('message', async function incoming(wsClientPacket){
		try{
				var wsc = JSON.parse(wsClientPacket)
//#region WebSocket client validation and identification
				//Validate websocket identity
				if (liveQuiz_WSS.validateIdentity(wsc)){
				} else{
					//Validate secret key identity
					if (await isAuthenticated(wsc)){
						liveQuiz_WSS.addIdentity(wsc)
					}
					else{
						ws.send(JSON.stringify({Success:false,Data:"Invalid identity"}))
						return
					}
				}
//#endregion
				switch(wsc.Action){
					case "JoinQuiz":
						if (liveQuizzes.hasOwnProperty(wsc.Identity.AccessCode)){
							//Add user to live quiz if needed
							if (!liveQuizzes[wsc.Identity.AccessCode].Students.hasOwnProperty(wsc.Identity.WSID)){
								let lq = shuffle(await getQuestionsOfQuizByAccessCode(wsc.Identity.AccessCode))
								liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID] = { Answers:[], CurrentQuestion:lq[0],LeftQuestions:lq}
								quizStatus_WSS.clients.forEach(function each(client) {
									if (client !== ws && client.readyState === WebSocket.OPEN) {
									  client.send(JSON.stringify({Action:"QuizProgressChanged", Data:liveQuizzes[wsc.Identity.AccessCode].Students}))
									}
								})
							}
							ws.send(JSON.stringify({Success:true}))
						}
						else{
							ws.send(JSON.stringify({Success:false,Data:"Invalid quiz ID"}))
						}
					break;
					case "NextQuestion":
						let currentQuestion = (await Questions.findAll({where:{ID:liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].CurrentQuestion}}))[0];
						ws.send(JSON.stringify({Success:true,Data:currentQuestion}))
					break;
					case "PostAnswer":
						let questionID = liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].CurrentQuestion;
						let answer = wsc.Data;
						liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].Answers.push({QuestionID:questionID, Answer:answer})
						liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].LeftQuestions = removeFirst(liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].LeftQuestions);
						if (liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].LeftQuestions.length > 0){
							liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].CurrentQuestion = liveQuizzes[wsc.Identity.AccessCode].Students[wsc.Identity.WSID].LeftQuestions[0];
							ws.send(JSON.stringify({Success:true}))
						}
						else{
							ws.send(JSON.stringify({Success:true, Data:"Completed!"}))
						}
						quizStatus_WSS.clients.forEach(function each(client) {
							if (client !== ws && client.readyState === WebSocket.OPEN) {
							  client.send(JSON.stringify({Action:"QuizProgressChanged", Data:liveQuizzes[wsc.Identity.AccessCode].Students}))
							}
						})
					break;
					default:
						ws.send(JSON.stringify({Success:false,Data:"Unknown"}))
					break;
				}
		}
		catch(e){
			console.log(e)
			ws.send(JSON.stringify({Success:false,Data:"Invalid JSON"}))
		}
	})
	ws.send(JSON.stringify({Success:true,Data:ws.id}))
})

liveQuiz_WSS.clientIdentities = {}

liveQuiz_WSS.validateIdentity = function(wsClientPacket){
	return liveQuiz_WSS.clientIdentities.hasOwnProperty(wsClientPacket.Identity.SecretKey) && liveQuiz_WSS.clientIdentities[wsClientPacket.Identity.SecretKey] == wsClientPacket.Identity.WSID
}

liveQuiz_WSS.addIdentity = function(wsClientPacket){
	liveQuiz_WSS.clientIdentities[wsClientPacket.Identity.SecretKey] = wsClientPacket.Identity.WSID
}

quizStatus_WSS.getUniqueID = function () {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
    }
    return s4() + s4() + '-' + s4();
};

async function getQuestionsOfQuizByAccessCode(qiz){
	try{
		let q = (await ActivatedQuizzes.findAll({where:{AccessCode : qiz}}))[0].QuizID
		return JSON.parse((await Quizzes.findAll({where:{ID:q}}))[0].Questions)
	}
	catch(e){
		console.log(e)
	}
}

function shuffle(array) {
    let counter = array.length;

    // While there are elements in the array
    while (counter > 0) {
        // Pick a random index
        let index = Math.floor(Math.random() * counter);

        // Decrease counter by 1
        counter--;

        // And swap the last element with it
        let temp = array[counter];
        array[counter] = array[index];
        array[index] = temp;
    }

    return array;
}

function removeFirst(arr){
	var temp = [];
	for (let i = 1; i < arr.length; i++)
		temp.push(arr[i]);
	return temp;
}

  //#endregion