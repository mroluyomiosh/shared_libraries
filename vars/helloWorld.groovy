def call(Map config = [:]) {
  node() {
    stage('Example') {
     sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
    }
  }
}
