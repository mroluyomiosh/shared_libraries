def call(Map config = [:]) {
  node() {
    stage('Example') {
      steps {
        sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
      }
    }
  }
}
