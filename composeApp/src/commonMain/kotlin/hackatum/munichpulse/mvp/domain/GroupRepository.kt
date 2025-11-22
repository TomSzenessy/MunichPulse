package hackatum.munichpulse.mvp.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface GroupRepository {
    fun getMyGroups(): Flow<List<Group>>
}

class MockGroupRepository : GroupRepository {
    private val groups = listOf(
        Group(
            id = "1",
            eventId = "1", // Tollwood
            members = listOf(
                User("1", "Alice", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true),
                User("2", "Bob", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", false),
                User("3", "Charlie", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true)
            )
        ),
        Group(
            id = "2",
            eventId = "3", // Open Air Kino
            members = listOf(
                User("4", "David", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", true),
                User("5", "Eve", "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnqZmET8ib9ye3aLMp1hzdx83rsDLhLVULmiB0q0VGCKLIOAwhQlGPB9IIeaRc0R_0VOLMb6vvCdwxQZJgE_6zHPmhC4DzPfiVcE8Uy4oxEfGoZ8RE4St7OzoKyiqducb2ycFd-fGovAv847DICaUifjadf3k7b5I2rKEST-xvtQxtKIMUQQGl2mfjWPvJ_cyYQ5yEHU3ZlaDdBbcgtYqWMxklrPlWMhvQQRhHF1MYiFFz8l0sEiOGZPmIicngq1glRco5qSBV4BR", false)
            )
        )
    )

    override fun getMyGroups(): Flow<List<Group>> = flowOf(groups)
}
