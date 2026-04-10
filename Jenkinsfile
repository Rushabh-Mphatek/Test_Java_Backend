pipeline {
    agent any

    environment {
        BASE_DIR = '/home/Rushabh/Test_Java_Backend'
        DOCKER_COMPOSE_FILE = 'stage1-docker-compose.yml'
    }

    stages {

        // 🔷 1. Initialize & Branch Setup
        stage('Initialize & Detect Branch') {
            steps {
                script {

                    env.BRANCH_NAME = env.BRANCH_NAME ?: "main"

                    env.SAFE_BRANCH_NAME = env.BRANCH_NAME
                        .replaceAll('[^a-zA-Z0-9._-]', '-')
                        .toLowerCase()

                    env.COMMIT_FILE = "/home/Rushabh/last_successful_commit_${env.SAFE_BRANCH_NAME}.txt"
                    env.TARGET_DIR = "/home/Rushabh/Target/${env.SAFE_BRANCH_NAME}"
                    env.COMPOSE_PROJECT_NAME = "backend-stage1-${env.SAFE_BRANCH_NAME}"

                    echo """
                    ╔════════════════════════════════════════════╗
                    ║   🌿 BRANCH DEPLOYMENT INITIALIZED         ║
                    ╚════════════════════════════════════════════╝
                    Branch Name       : ${env.BRANCH_NAME}
                    Safe Branch Name  : ${env.SAFE_BRANCH_NAME}
                    Commit File       : ${env.COMMIT_FILE}
                    Target Directory  : ${env.TARGET_DIR}
                    Docker Project    : ${env.COMPOSE_PROJECT_NAME}
                    ════════════════════════════════════════════
                    """
                }
            }
        }

        // 🔷 2. Checkout Code
        stage('Checkout Code') {
            steps {
                script {
                    dir("${BASE_DIR}") {

                        sh """
                            if [ -d .git ]; then
                                git reset --hard
                                git clean -fd
                            else
                                cd ..
                                rm -rf Test_Java_Backend
                                git clone <YOUR_REPO_URL> Test_Java_Backend
                            fi
                        """

                        sh """
                            git fetch --all --prune
                            git checkout ${env.BRANCH_NAME} || git checkout -b ${env.BRANCH_NAME} origin/${env.BRANCH_NAME}
                            git reset --hard origin/${env.BRANCH_NAME}
                        """

                        sh "git log -1 --oneline"
                    }
                }
            }
        }

        // 🔷 3. Detect Changed Services
        stage('Detect Changed Services') {
            steps {
                script {
                    dir("${BASE_DIR}") {

                        def currCommit = sh(script: "git rev-parse HEAD", returnStdout: true).trim()
                        def prevCommit = fileExists(env.COMMIT_FILE) ? readFile(env.COMMIT_FILE).trim() : ""

                        def changedFiles = ""

                        if (!prevCommit) {
                            changedFiles = "FIRST_BUILD"
                        } else {
                            changedFiles = sh(
                                script: "git diff --name-only ${prevCommit} ${currCommit}",
                                returnStdout: true
                            ).trim()
                        }

                        def allServices = sh(
                            script: "ls -d */ | grep -v '@tmp' | cut -f1 -d'/'",
                            returnStdout: true
                        ).trim().split("\n")

                        def changedServices = []

                        if (changedFiles == "FIRST_BUILD") {
                            changedServices = allServices
                        } else if (changedFiles) {
                            def services = changedFiles
                                .split("\n")
                                .collect { it.tokenize('/')[0] }
                                .unique()

                            for (svc in services) {
                                if (fileExists("${svc}/pom.xml")) {
                                    changedServices.add(svc)
                                }
                            }
                        }

                        env.CHANGED_SERVICES = changedServices.join(",")
                        env.ALL_SERVICES_CHANGED =
                            (changedServices.size() == allServices.size()) ? "true" : "false"

                        writeFile file: env.COMMIT_FILE, text: currCommit

                        echo "Changed: ${env.CHANGED_SERVICES}"
                    }
                }
            }
        }

        // 🔷 4. Build Services
        stage('Build Changed Services') {
            steps {
                script {

                    if (!env.CHANGED_SERVICES?.trim()) return

                    def services = env.CHANGED_SERVICES.tokenize(',')

                    for (svc in services) {
                        dir("${BASE_DIR}/${svc}") {
                            sh """
                                rm -rf target
                                if [ -f mvnw ]; then
                                    chmod +x mvnw
                                    ./mvnw clean package -DskipTests
                                else
                                    mvn clean package -DskipTests
                                fi
                            """
                        }
                    }
                }
            }
        }

        // 🔷 5. Move JAR
        stage('Backup & Move JAR') {
            steps {
                script {

                    if (!env.CHANGED_SERVICES?.trim()) return

                    def services = env.CHANGED_SERVICES.tokenize(',')

                    for (svc in services) {

                        def sourcePath = "${BASE_DIR}/${svc}/target"
                        def destinationPath = "${env.TARGET_DIR}/${svc}"

                        sh """
                            mkdir -p ${destinationPath}

                            NEW_JAR=\$(ls ${sourcePath}/*.jar | grep -v 'original' | head -n 1)

                            if [ -z "\$NEW_JAR" ]; then exit 1; fi

                            cp -f "\$NEW_JAR" "${destinationPath}/"
                        """
                    }
                }
            }
        }

        // 🔷 6. Docker Cleanup
        stage('Docker Cleanup') {
            when {
                expression { env.ALL_SERVICES_CHANGED == "true" }
            }
            steps {
                dir("${BASE_DIR}") {
                    sh """
                        docker compose -p ${COMPOSE_PROJECT_NAME} -f ${DOCKER_COMPOSE_FILE} down --remove-orphans || true
                        docker image prune -f --filter "until=24h" || true
                    """
                }
            }
        }

        // 🔷 7. Load Base Images
        stage('Load Base Images') {
            steps {
                sh """
                    if [ -f /home/Rushabh/docker-base-images/base-images.tar ]; then
                        docker load -i /home/Rushabh/docker-base-images/base-images.tar
                    else
                        exit 1
                    fi
                """
            }
        }

        // 🔷 8. Prepare JAR for Docker
        stage('Prepare JAR for Docker') {
            steps {
                script {

                    if (!env.CHANGED_SERVICES?.trim()) return

                    def services = env.CHANGED_SERVICES.tokenize(',')

                    for (svc in services) {
                        sh """
                            JAR_FILE=\$(ls ${env.TARGET_DIR}/${svc}/*.jar | head -n 1)
                            cp "\$JAR_FILE" ${BASE_DIR}/${svc}/app.jar
                        """
                    }
                }
            }
        }

        // 🔷 9. Full Deploy
        stage('Docker Full Deploy') {
            when {
                expression { env.ALL_SERVICES_CHANGED == "true" }
            }
            steps {
                dir("${BASE_DIR}") {
                    sh """
						docker compose -f ${DOCKER_COMPOSE_FILE} down || true
                        docker compose -f ${DOCKER_COMPOSE_FILE} build --no-cache
                        docker compose -f ${DOCKER_COMPOSE_FILE} up -d
                    """
                }
            }
        }

        // 🔷 10. Partial Deploy
        stage('Docker Partial Deploy') {
            when {
                expression { env.ALL_SERVICES_CHANGED != "true" }
            }
            steps {
                script {

                    def services = env.CHANGED_SERVICES.tokenize(',')
                    def parallelDeploy = [:]

                    for (svc in services) {
                        parallelDeploy["Deploy-${svc}"] = {
                            dir("${BASE_DIR}") {
                                sh """
                                    docker compose -f ${DOCKER_COMPOSE_FILE} up -d --no-deps --build ${svc}
                                """
                            }
                        }
                    }

                    parallel parallelDeploy
                }
            }
        }

        // 🔷 11. Cleanup app.jar
        stage('Cleanup app.jar') {
			steps {
				script {
					if (!env.CHANGED_SERVICES?.trim()) return

					env.CHANGED_SERVICES.tokenize(',').each { svc ->
						sh "rm -f ${BASE_DIR}/${svc}/app.jar || true"
					}
				}
			}
		}

        // 🔷 12. Summary
        stage('Final Summary') {
            steps {
                script {
                    if (!env.CHANGED_SERVICES?.trim()) return

                    env.CHANGED_SERVICES.tokenize(',').each { svc ->

                        def status = sh(
                            script: "docker ps -a --filter name=${svc} --format '{{.Status}}' || true",
                            returnStdout: true
                        ).trim()

                        echo "Service: ${svc} → ${status}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 SUCCESS: ${env.BRANCH_NAME} deployed"
        }
        failure {
            echo "❌ FAILURE"
        }
    }
}