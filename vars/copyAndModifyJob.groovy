// vars/copyAndModifyJob.groovy

def call(Map params) {
    def sourceJob = params.SOURCE_JOB
    def targetJob = params.TARGET_JOB
    def parameters = params.PARAMETERS

    node {
        stage('Copy Job') {
            echo "Copying job ${sourceJob} to ${targetJob}..."
            // Use Jenkins REST API to copy job
            withCredentials([string(credentialsId: 'jenkins-api-token', variable: 'JENKINS_TOKEN')]) {
                def jenkinsUrl = env.JENKINS_URL
                def auth = '${env.JENKINS_USER}:${env.JENKINS_TOKEN}'

                sh '''
                    curl -X POST -u ${auth} ${jenkinsUrl}/createItem?name=${targetJob}&mode=copy&from=${sourceJob}
                '''
            }
        }

        // stage('Modify Job') {
        //     echo "Modifying job ${targetJob}..."
        //     // Get the job configuration using Jenkins REST API
        //     withCredentials([string(credentialsId: 'jenkins-api-token', variable: 'JENKINS_TOKEN')]) {
        //         def auth = '${env.JENKINS_USER}:${env.JENKINS_TOKEN}'

        //         def jobConfig = sh(script: '''
        //             curl -s -u ${auth} ${env.JENKINS_URL}/job/${targetJob}/config.xml
        //         ''', returnStdout: true).trim()

        //         // Replace parameters in jobConfig
        //         parameters.each { key, value ->
        //             jobConfig = jobConfig.replaceAll(key, value)
        //         }

        //         // Update job configuration using Jenkins REST API
        //         writeFile(file: 'job-config.xml', text: jobConfig)
        //         sh '''
        //             curl -X POST -u ${auth} -H "Content-Type: application/xml" --data-binary @job-config.xml ${env.JENKINS_URL}/job/${targetJob}/config.xml
        //         '''
        //     }
        // }
    }
}