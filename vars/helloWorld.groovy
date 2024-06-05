def call(Map config = [:]) {
  def buildNode = "${config.BUILD_NODE}"
    node(buildNode) {
      stage('Example') {
       sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
      }
    }
}
