0. Install gpg:  https://gnupg.org/download/index.html

A - Generate Encrypted Properties
1. create unencrypted properties file (annotation.user.properties)
2. encript file:
cd ./.github/workflows
gpg --symmetric --cipher-algo AES256 annotation.user.properties
3. when prompted provide encyption password
4. if successfull annotation.user.properties.gpg file are created
4.1 delete annotation.user.properties
4.2 save used encryption password in github secret (e.g. ANNOTATION_API_PROPS_ENCODING_PASS)
(NOTE: ANNOTATION_API_PROPS_ENCODING_PASS is the name of the secret in Github secrets see Settings-> Secrets and Variables -> Actions -> Secrets)

B - Use Encrypted Properties
1. Create & configure decrypt script (decrypt_config.sh)
1.1 ensure executions rights before committing to github: 
#on Linux
chmod +x decrypt_config.sh
ls -l decrypt_config.sh

#on Windows cygwin or WSL or Git Bash can be used
git add --chmod=+x  ./.github/workflows/decrypt_config.sh
#if the file was allready added to git, use the following
#git update-index --chmod=+x ./.github/workflows/decrypt_config.sh
#verify executioin permissions
git ls-files --stage
1.2 commit file to gihub
git commit -m"Executable script!"


2. Ensure that the application is able to load properties from external file and that the decripted properties file matches the location from which the app is loading properties 
3. Update Github Workflow to run decrypt script (NOTE: secrets.ANNOTATION_API_PROPS_ENCODING_PASS is the name of the secret in Github secrets see Settings-> Secrets and Variables -> Actions -> Secrets)

 ....
 steps:
      - uses: actions/checkout@v5
	  ....
      - name: Decrypt configuration properties
		#relative to base project folder
        run: ./.github/workflows/decrypt_config.sh
        env:
          SECRET_PROPS_PASSPHRASE: ${{ secrets.ANNOTATION_API_PROPS_ENCODING_PASS }}
	   .....  
	   
4. Verify successfull execution of system integration tests in Github