import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.util.concurrent.*
val knownProjectsByPath: ConcurrentHashMap<String, File> = ConcurrentHashMap(100)

fun main() {
    initCache()
    Thread.sleep(TimeUnit.SECONDS.toMillis(5))
    println("Found these $knownProjectsByPath")
}

fun initCache(rootDir: String = System.getenv("HOME")) {
    GlobalScope.async(Dispatchers.IO) {
        val processMorDirsChannel = Channel<File>(20000)
        val receiveGitDirsChannel = Channel<File>(2000)
        File(rootDir).processDir(processMorDirsChannel, receiveGitDirsChannel)

        launch(Dispatchers.IO) {
            receiveGitDirsChannel.consumeEach {
                knownProjectsByPath[it.parentFile.name] = it.parentFile
            }
        }
        launch(Dispatchers.IO) {
            processMorDirsChannel.consumeEach {
                it.processDir(processMorDirsChannel, receiveGitDirsChannel)
            }
        }
    }
}
private suspend fun File.processDir(processSubDirsChannel: Channel<File>, processGitDirChannel: Channel<File>) {
    GlobalScope.async(Dispatchers.IO) {
        listFiles { file, _ -> file.isDirectory }
            ?.forEach { subDir ->
                if (!Files.isSymbolicLink(subDir.toPath())) {
                    processSubDirsChannel.send(subDir)
                    if (subDir.endsWith(".git")) {
                        processGitDirChannel.send(subDir)
                    }
                }
            }
    }
}
