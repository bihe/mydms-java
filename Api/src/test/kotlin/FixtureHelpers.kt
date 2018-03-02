import java.util.*

object FixtureHelpers {

    fun fixture(path: String): String {
        return Scanner(FixtureHelpers::class.java.classLoader.getResourceAsStream(path), "UTF-8").useDelimiter("\\A").next()
    }
}