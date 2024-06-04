// Function to copy a Jenkins job
def call(String originalJobName, String newJobName) {
    def job = Jenkins.instance.getItem(originalJobName)
    def newJob = job.copy(job.parent, newJobName)
    newJob.displayName = "New Job: " + newJobName
    newJob.save()
}
