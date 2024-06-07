// vars/modifyAndCommitFile.groovy
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def call(Map params) {
    node {
        def repoUrl = params.REPO_URL
        def originalFile = params.ORIGINAL_FILE
        def newFileName = params.NEW_FILE_NAME
        def productName = params.PRODUCT_NAME
        def applicationName = params.APPLICATION_NAME
        def testApplicationName = params.TEST_APPLICATION_NAME
        def branchName = params.BRANCH_NAME

        stage('Prepare Workspace') {
            echo "Preparing workspace..."
            // Clean the workspace directory
            deleteDir()
        }

        stage('Checkout') {
            echo "Checking out repository..."
            // Clone the repository
            sh """
                git config --global user.email "mroluyomiosh@gmail.com"
                git config --global user.name "mroluyomiosh"
                git clone ${repoUrl} .
                git checkout main
            """
        }
        
        stage('Create Feature Branch') {
            echo "Creating feature branch ${branchName}..."
            // Create a new feature branch
            sh "git checkout -b ${branchName}"
        }
        
        stage('Copy and Modify File') {
            echo "Copying and modifying file ${originalFile} to ${newFileName}..."
            // Copy and rename the file
            sh "cp ${originalFile} ${newFileName}"

            // Modify the new file
            sh """
                sed -i 's/ProductName="abc"/ProductName="${productName}"/' ${newFileName}
                sed -i 's/ApplicationName="xyz"/ApplicationName="${applicationName}"/' ${newFileName}
                sed -i 's/TestApplicationName="uvw"/TestApplicationName="${testApplicationName}"/' ${newFileName}
            """
        }
        
        stage('Commit and Push Changes') {
            echo "Committing and pushing changes..."
            // Add, commit, and push changes
            withCredentials([gitUsernamePassword(credentialsId: 'github-token', gitToolName: 'Default')]) {
                sh """
                    git add ${newFileName}
                    git commit -m 'Modified ${newFileName} with new parameters'
                    git push origin ${branchName}
                """
            }
        }
        
        def pullRequestNumber = null
        
        stage('Create Pull Request') {
            echo "Creating pull request..."
            // Create a pull request using GitHub API
            def payload = JsonOutput.toJson([
                title: "Feature: Modify ${newFileName}",
                body: "This pull request modifies the ${newFileName} with new parameters.",
                head: branchName,
                base: "main"
            ])
            
            withCredentials([gitUsernamePassword(credentialsId: 'github-token', gitToolName: 'Default')]) {
                def response = sh(script: """
                    curl -X POST -H "Authorization: token ${env.GIT_PASSWORD}" -H "Content-Type: application/json" \
                    -d '${payload}' https://api.github.com/repos/${repoUrl.split('/')[3]}/${repoUrl.split('/')[4].replace('.git', '')}/pulls
                """, returnStdout: true).trim()
                
                def jsonResponse = new JsonSlurper().parseText(response)
                pullRequestNumber = jsonResponse.number.toString()
                echo "Created Pull Request #${pullRequestNumber}"
            }
        }

        stage('Wait for Pull Request to be Merged') {
            echo "Waiting for pull request #${pullRequestNumber} to be merged..."
            def isMerged = false
            while (!isMerged) {
                withCredentials([gitUsernamePassword(credentialsId: 'github-token', gitToolName: 'Default')]) {
                    def response = sh(script: """
                        curl -H "Authorization: token ${env.GIT_PASSWORD}" \
                        https://api.github.com/repos/${repoUrl.split('/')[3]}/${repoUrl.split('/')[4].replace('.g
