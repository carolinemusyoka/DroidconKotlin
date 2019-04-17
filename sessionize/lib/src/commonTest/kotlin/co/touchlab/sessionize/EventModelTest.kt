package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.platform.TestConcurrent
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest

import kotlin.test.Test
import kotlin.test.assertTrue

abstract class EventModelTest {
    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()

    @BeforeTest
    fun setup() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                Dispatchers.Main, TestSettings(), TestConcurrent, SessionizeApiMock(), AnalyticsApiMock(), "-0400")

        ServiceRegistry.initLambdas({filePrefix, fileType ->
            when(filePrefix){
                "sponsors" -> SPONSORS
                "speakers" -> SPEAKERS
                "schedule" -> SCHEDULE
                else -> SCHEDULE
            }
        }, {s: String -> Unit})

        AppContext.initAppContext()

        AppContext.seedFileLoad()
    }

    @Test
    fun testRsvpAndAnalytics() = runTest {
        val eventModel = EventModel("67316")
        val session = AppContext.sessionQueries.sessionById("67316").executeAsOne()
        val si = collectSessionInfo(session)
        /*eventModel.toggleRsvpSuspend(si)
        assertTrue { sessionizeApiMock.rsvpCalled }
        assertTrue { analyticsApiMock.logCalled }*/
    }
}

class AnalyticsApiMock : AnalyticsApi {
    var logCalled = false

    override fun logEvent(name: String, params: Map<String, Any>) {
        logCalled = true
    }

}

class SessionizeApiMock : SessionizeApi {
    var rsvpCalled = false
    override suspend fun getSpeakersJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSessionsJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSponsorJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean {
        rsvpCalled = true
        return true
    }
}