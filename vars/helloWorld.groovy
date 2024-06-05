buildNode = "${config.BUILD_NODE}"
def call(Map config = [:]) {
  node(buildNode) {
    stage('Example') {
     sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
    }
  }
}
