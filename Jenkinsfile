pipeline {
    agent any

    environment {
        BASE_DIR = '/home/Rushabh/Test_Java_Backend'
    }

    stages {

        // ✅ 1. Initialize & Detect Branch (SAFE)
        stage('Initialize & Detect Branch') {
            steps {
                script {

                    // Jenkins provides BRANCH_NAME in multibranch
                    env.BRANCH_NAME = env.BRANCH_NAME ?: "main"

                    // Sanitize branch name
                    env.SAFE_BRANCH_NAME = env.BRANCH_NAME
                        .replaceAll('[^a-zA-Z0-9._-]', '-')
                        .toLowerCase()

                    // Dynamic paths (SAFE now)
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
                    Build Number      : ${env.BUILD_NUMBER}
                    ════════════════════════════════════════════
                    """
                }
            }
        }

        // ✅ 2. Checkout Code (CRITICAL FIX)
        stage('Checkout Code') {
            steps {
                script {
                    dir("${BASE_DIR}") {

                        // Clean previous repo (important for branch switching)
                        sh """
                            if [ -d .git ]; then
                                echo "🧹 Cleaning existing repo"
                                git reset --hard
                                git clean -fd
                            else
                                echo "📥 Fresh clone"
                                cd ..
                                rm -rf Test_Java_Backend
                                git clone <YOUR_REPO_URL> Test_Java_Backend
                            fi
                        """

                        // 🔥 Correct branch checkout
                        sh """
                            git fetch --all --prune
                            git checkout ${env.BRANCH_NAME} || git checkout -b ${env.BRANCH_NAME} origin/${env.BRANCH_NAME}
                            git reset --hard origin/${env.BRANCH_NAME}
                        """

                        echo "✅ Checked out branch: ${env.BRANCH_NAME}"

                        sh "git log -1 --oneline"
                    }
                }
            }
        }

        // ✅ 3. Detect Changed Services
        stage('Detect Changed Services') {
            steps {
                script {
                    dir("${BASE_DIR}") {

                        def currCommit = sh(
                            script: "git rev-parse HEAD",
                            returnStdout: true
                        ).trim()

                        def prevCommit = fileExists(env.COMMIT_FILE) ?
                            readFile(env.COMMIT_FILE).trim() : ""

                        def changedFiles = ""

                        if (!prevCommit) {
                            echo "🆕 FIRST BUILD for ${env.BRANCH_NAME}"
                            changedFiles = "FIRST_BUILD"
                        } else {
                            echo "📊 Comparing commits:"
                            echo "   Previous: ${prevCommit}"
                            echo "   Current:  ${currCommit}"

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
                        } else {
                            echo "⚠️  No file changes detected"
                        }

                        env.CHANGED_SERVICES = changedServices.join(",")

                        env.ALL_SERVICES_CHANGED =
                            (changedServices.size() == allServices.size()) ? "true" : "false"

                        echo """
                        📊 Detection Results:
                        ─────────────────────────────────────────
                        Changed Services: ${env.CHANGED_SERVICES ?: 'NONE'}
                        All Changed:      ${env.ALL_SERVICES_CHANGED}
                        Service Count:    ${changedServices.size()} / ${allServices.size()}
                        ─────────────────────────────────────────
                        """

                        // Save commit
                        writeFile file: env.COMMIT_FILE, text: currCommit
                    }
                }
            }
        }

        // ✅ 4. Build Changed Services
        stage('Build Changed Services') {
            steps {
                script {

                    if (!env.CHANGED_SERVICES?.trim()) {
                        echo "⏭️  No services to build"
                        return
                    }

                    def services = env.CHANGED_SERVICES.tokenize(',')

                    for (svc in services) {
                        dir("${BASE_DIR}/${svc}") {

                            echo "🔨 Building: ${svc}"

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

        // ✅ 5. Backup & Move JAR
        stage('Backup & Move JAR') {
            steps {
                script {

                    if (!env.CHANGED_SERVICES?.trim()) {
                        return
                    }

                    def services = env.CHANGED_SERVICES.tokenize(',')

                    for (svc in services) {

                        def sourcePath = "${BASE_DIR}/${svc}/target"
                        def destinationPath = "${env.TARGET_DIR}/${svc}"

                        sh """
                            mkdir -p ${destinationPath}

                            NEW_JAR=\$(ls ${sourcePath}/*.jar | grep -v 'original' | head -n 1)

                            if [ -z "\$NEW_JAR" ]; then
                                echo "❌ No JAR found for ${svc}"
                                exit 1
                            fi

                            EXISTING_JAR=\$(ls ${destinationPath}/*.jar 2>/dev/null | grep -v '_' | head -n 1 || true)

                            if [ ! -z "\$EXISTING_JAR" ]; then
                                TIMESTAMP=\$(date +"%d-%m-%Y_%I-%M-%S_%p")
                                BASENAME=\$(basename "\$EXISTING_JAR" .jar)
                                mv "\$EXISTING_JAR" "${destinationPath}/\${BASENAME}_\${TIMESTAMP}.jar"
                            fi

                            cp -f "\$NEW_JAR" "${destinationPath}/"
                            echo "✅ Deployed ${svc}"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "🎉 SUCCESS: ${env.BRANCH_NAME} deployed successfully"
        }
        failure {
            echo "❌ FAILURE: Check logs"
        }
    }
}