IF DB_ID('quyzygy') IS NULL
BEGIN
    CREATE DATABASE [Quyzygy];
END

IF NOT EXISTS (SELECT * FROM dbo.sysobjects where id = object_id(N'dbo.[ApplicationUsers]') and OBJECTPROPERTY(id, N'IsTable') = 1)
	BEGIN
		CREATE TABLE [ApplicationUsers](
		AccessFailedCount varchar(3),
		ConcurrencyStamp varchar(50),
		Email varchar(50),
		EmailConfirmed BIT,
		ID varchar(100),
		LockoutEnabled BIT,
		LockoutEnd DATETIME,
		Name varchar(50), 
		NormalizedEmail varchar(50),
		NormalizedUserName varchar(50),
		PasswordHash varchar(128),
		PhoneNumber varchar(15),
		PhoneNumberConfirmed BIT,
		SecurityStamp varchar(50),
		Surname varchar(50),
		TwoFactorEnabled BIT,
		Username varchar(50)
		);
	END