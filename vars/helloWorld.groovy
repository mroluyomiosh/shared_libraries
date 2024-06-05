def call(Map config = [:]) {
  node('any') {
    stage('Example') {
      steps {
        sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
      }
    }
  }
}
