// Function to create a branch and modify files
def call(Map params) {
    def applicationName = params.applicationName
    def newBranchName = params.newBranchName
    sh """
        cd ${WORKSPACE}
        git fetch origin main
        git checkout main
        git checkout -b ${newBranchName}
        git push -u origin ${newBranchName}

        # Modify Jenkinsfile
        sed -i 's/PLACEHOLDER/${applicationName}/g' Jenkinsfile
        git add Jenkinsfile
        git commit -m "Update Jenkinsfile for ${applicationName}"
        git push origin ${newBranchName}

        # Create PR using GitHub CLI
        gh pr create --base main --head ${newBranchName} --title "Onboard ${applicationName}" --body "Auto-generated PR for application onboarding."
    """
}
