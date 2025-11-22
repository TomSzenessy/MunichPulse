package hackatum.munichpulse.mvp.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface UserRepository {
    fun getCurrentUser(): Flow<User>
    fun getLogbookEntries(): Flow<List<LogbookEntry>>
}

class MockUserRepository : UserRepository {
    private val currentUser = User(
        id = "me",
        name = "Wanderlust",
        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQts8JbmFxIyYdug0PKmZl5OdhBLP9hTKxz_uKAnvZpuac-30tETuTBqRdPE5DEPk5hGdiNUmR-lJ0TMYJd9x8vnY8MAnOUuRtk_Jxmnq_35gj4zrpxZBXXxQRWPEfbXQjl2HAwfxM3A7w_cffrbD-w42pStMjw3Xy16IVJblj48904FPZMfxkyduNsXQBWhVSAVdLq_SbrEQbfJZJCXmXbYCk6CZCMyZ6-vXnpoR1nrxlPFW3EaC_KF9HD_GeA6bx_ua7c20OJft0",
        isLocal = true
    )

    private val logbookEntries = listOf(
        LogbookEntry("1", "Oktoberfest - Tent 4", 12.3, 87, 1698000000000),
        LogbookEntry("2", "Bahnwärter Thiel - Rave", 5.8, 62, 1697000000000),
        LogbookEntry("3", "Tollwood Festival", 2.1, 45, 1696000000000)
    )

    override fun getCurrentUser(): Flow<User> = flowOf(currentUser)
    override fun getLogbookEntries(): Flow<List<LogbookEntry>> = flowOf(logbookEntries)
}
