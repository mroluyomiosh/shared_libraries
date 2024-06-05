def call(Map config = [:]) {
  node() {
    stage('Example') {
      scripts {
        sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
      }
    }
  }
}
