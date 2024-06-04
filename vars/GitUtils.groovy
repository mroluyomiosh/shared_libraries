// vars/GitUtils.groovy
def createBranch(String branchName) {
    sh "git checkout main"
    sh "git pull origin main"
    sh "git checkout -b ${branchName}"
    sh "git push origin ${branchName}"
}

def modifyAndPushFile(String branchName, String filePath, String oldValue, String newValue) {
    sh """
        git checkout ${branchName}
        sed -i 's/${oldValue}/${newValue}/g' ${filePath}
        git add ${filePath}
        git commit -m 'Modify ${filePath}'
        git push origin ${branchName}
    """
}

def createPullRequest(String branchName, String title, String body) {
    sh "gh pr create --base main --head ${branchName} --title '${title}' --body '${body}'"
}
