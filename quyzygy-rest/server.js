'use strict'
var express = require('express');
var bodyParser = require('body-parser');
const Sequelize = require('sequelize');
const sha256 = require('sha256');
const Op = Sequelize.Op;

const app = express();
const sequelize = new Sequelize('quyzygy_db','root','',{
	dialect : 'mysql',
	define : {
		timestamps : false
	}
});

app.use(bodyParser.json()); // Accept  JSON Params
app.use(bodyParser.urlencoded({extended:true})); // Accept URL encoded params

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
	        len : 64
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
})

const Questions = sequelize.define('Questions', {
	ID: {
		type: Sequelize.INTEGER,
		allowNull: false
	},
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
		allowNull:false,
		len:[0,4196]
	},
	Points:{
		type:Sequelize.INTEGER,
		allowNull:false
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

//#endregion

//#region Database manipulation

//#region Authentication and registration

app.post('/login', async (req, res)=>{
	try {
		let result = await Users.findAndCountAll({
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
			if (existingSKs.len == 1)
				await AuthenticatedUsers.insertOrUpdate({Email:req.param('email'), SecretKeys:JSON.stringify(existingSKs)})
			else
				await AuthenticatedUsers.update({Email:req.param('email'), SecretKeys:JSON.stringify(existingSKs)},
			{where: { email: req.param('email')}})
			res.status(200).json({secretKey:sk})
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

//#endregion

//#region Question manipulation

app.post('/createQuestion', async (req, res) => {
	try{
		if (!await validateUser(req)){
			res.status(401).json({error:'Unauthorized'})
			return
		}
		await Questions.create(req.body)
		res.status(201).json({message : 'created'})
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

async function getEmailForLoggedUser(request){
	try {
		var sel = await AuthenticatedUsers.findAll()
		return sel[0].Email
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

//User.create({firstName:'Ioana',lastName:'Pasarin',email:'ioana.pasarin@ase.ro',passwordHash:'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',userType:'Student'});
app.listen(8080)