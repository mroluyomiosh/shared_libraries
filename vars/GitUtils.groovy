// vars/GitUtils.groovy
def checkoutRepo(String repoUrl, String credentialsId) {
    checkout([$class: 'GitSCM', branches: [[name: '*/main']],
              userRemoteConfigs: [[url: repoUrl, credentialsId: credentialsId]]])
}

def createBranch(String branchName) {
    sh "git checkout main"
    sh "git pull"
    sh "git checkout -b ${branchName}"
    sh "git push origin ${branchName}"
}

def modifyAndPushFile(String branchName, String filePath, String oldValue, String newValue) {
    sh """
        git checkout ${branchName}
        sed -i 's/${oldValue}/${newValue}/g' ${filePath}
        git add ${filePath}
        git commit -m 'Modify ${filePath} in ${branchName}'
        git push origin ${branchName}
    """
}

def createPullRequest(String branchName, String title, String body) {
    sh "gh pr create --base main --head ${branchName} --title '${title}' --body '${body}'"
}
