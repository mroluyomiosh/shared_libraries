// vars/copyAndModifyJob.groovy

def call(Map params) {
    def sourceJob = params.SOURCE_JOB
    def targetJob = params.TARGET_JOB
    def parameters = params.PARAMETERS

    node {
        stage('Debug Environment Variables') {
            echo "JENKINS_URL: ${env.JENKINS_URL}"
            echo "JENKINS_USER: ${env.JENKINS_USER}"
        }

        stage('Copy Job') {
            echo "Copying job ${sourceJob} to ${targetJob}..."
            withCredentials([string(credentialsId: 'jenkins-api-token', variable: 'JENKINS_TOKEN')]) {
                def jenkinsUrl = env.JENKINS_URL
                def auth = "${env.JENKINS_USER}:${env.JENKINS_TOKEN}"

                def response = sh(script: """
                    curl -L -s -o /dev/null -w "%{http_code}" -X POST -u ${auth} "${jenkinsUrl}/createItem?name=${targetJob}&mode=copy&from=${sourceJob}"
                """, returnStdout: true).trim()

                if (response != '200' && response != '201') {
                    error "Failed to copy job. HTTP response code: ${response}"
                } else {
                    echo "Job ${sourceJob} successfully copied to ${targetJob}. HTTP response code: ${response}"
                }
            }
        }
    }
}
