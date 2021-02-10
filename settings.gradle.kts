rootProject.name = "minicron"

buildCache {
    local {
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 10
    }
}