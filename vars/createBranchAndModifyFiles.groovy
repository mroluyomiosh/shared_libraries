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
            // Clean the workspace directory
            deleteDir()
        }

        stage('Checkout') {
            // Clone the repository
            sh """
                git config --global user.email "mroluyomiosh@gmail.com"
                git config --global user.name "mroluyomiosh"
                git clone ${repoUrl} .
                git checkout main
            """
        }
        
        stage('Create Feature Branch') {
            // Create a new feature branch
            sh "git checkout -b ${branchName}"
        }
        
        stage('Copy and Modify File') {
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
            // Add, commit, and push changes
            withCredentials([gitUsernamePassword(credentialsId: 'github-token', gitToolName: 'Default')]) {
                sh """
                    git add ${newFileName}
                    git commit -m 'Modified ${newFileName} with new parameters'
                    git push origin ${branchName}
                """
            }
        }
        
        stage('Create Pull Request') {
            // Create a pull request using GitHub API
            def payload = JsonOutput.toJson([
                title: "Feature: Modify ${newFileName}",
                body: "This pull request modifies the ${newFileName} with new parameters.",
                head: branchName,
                base: "main"
            ])
            
            withCredentials([gitUsernamePassword(credentialsId: 'github-token', gitToolName: 'Default')]) {
                sh """
                    curl -X POST -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/json" \
                    -d '${payload}' https://api.github.com/repos/${repoUrl.split('/')[3]}/${repoUrl.split('/')[4].replace('.git', '')}/pulls
                """
            }
        }

        stage('Check Pull Request Status') {
            // Check if the pull request has been merged
            def prNumber = sh(script: """
                curl -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/json" \
                https://api.github.com/repos/${repoUrl.split('/')[3]}/${repoUrl.split('/')[4].replace('.git', '')}/pulls | \
                jq '.[] | select(.head.ref=="${branchName}") | .number' | head -1
            """, returnStdout: true).trim()

            def prMerged = sh(script: """
                curl -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/json" \
                https://api.github.com/repos/${repoUrl.split('/')[3]}/${repoUrl.split('/')[4].replace('.git', '')}/pulls/${prNumber}/merge
            """, returnStatus: true) == 204

            if (prMerged) {
                echo "Pull request #${prNumber} has been merged."
            } else {
                error "Pull request #${prNumber} has not been merged yet."
            }
        }
    }
}
