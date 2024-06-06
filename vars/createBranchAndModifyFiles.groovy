// vars/modifyAndCommitFile.groovy
import groovy.json.JsonOutput

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
            sh 'rm -rf ${WORKSPACE}/*'
        }

        stage('Checkout') {
            // Clone the repository
            sh """
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
            sh """
                git add ${newFileName}
                git commit -m 'Modified ${newFileName} with new parameters'
                git push origin ${branchName}
            """
        }
        
        stage('Create Pull Request') {
            // Create a pull request using GitHub API
            def payload = JsonOutput.toJson([
                title: "Feature: Modify ${newFileName}",
                body: "This pull request modifies the ${newFileName} with new parameters.",
                head: branchName,
                base: "main"
            ])
            
            withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                sh """
                    curl -X POST -H "Authorization: token ${GITHUB_TOKEN}" -H "Content-Type: application/json" \
                    -d '${payload}' https://api.github.com/repos/${repoUrl.split('/')[3]}/${repoUrl.split('/')[4].replace('.git', '')}/pulls
                """
            }
        }
    }
}
