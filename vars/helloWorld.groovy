def call(Map config = [:]) {
  pipeline {
    agent any
    stages {
      stage('Example') {
        steps {
          sh "echo Hello ${config.name}. Today is ${config.dayOfWeek}."
        }
      }
    }
  }
}
