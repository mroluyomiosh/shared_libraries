// vars/JenkinsJobUtils.groovy
def copyJob(String originalJobName, String newJobName, String folderName) {
    sh """
        jenkins-jobs --conf /etc/jenkins_jobs/jenkins_job.ini copy ${originalJobName} ${newJobName}
        // Additional commands to move job to ${folderName}
    """
}
