package hackatum.munichpulse.mvp.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface EventRepository {
    fun getTrendingEvents(): Flow<List<Event>>
    fun getNearbyEvents(): Flow<List<Event>>
    fun getAllEvents(): Flow<List<Event>>
}

class MockEventRepository : EventRepository {
    private val events = listOf(
        Event(
            id = "1",
            title = "Tollwood Festival",
            location = "Olympiapark Süd",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuARp07lOoSMJ0Vi0n70MTVqcPnb7AIvrkaDNJG6S9WkBY-wnUR3IKzMyFLzcg0H8DsrvW8o29d7OiXlvJghqvGdVPqXffihptmghZqAYNlLaj_2n3aGjrVzOvx8QY9MHadn17cLoCzI5f0spIN0tvdIbMho2yKiorks4K-jkuHIjHwFEJX0jK0eK71OXP2wprm3_8B_2_jASWw8_bA26eMxTFILBdq1ZPZy9cFe_XyIvH4Wls54nJ9ZZttTPLWnkbRncAa3jLip1TWI",
            fullnessPercentage = 25,
            isTrending = true
        ),
        Event(
            id = "2",
            title = "Blade Night",
            location = "Bavariapark",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAiuPnZ9n2gHkIOOmG_UTy9hBthXvbsnU21vcH7mJ-9mDSr23C6kKB5T9rR_977ClHtPVScuqbzMYc3D27XR6ww5knIJGMM08Rb4IbJj6oxvTZwhjVdK8Z6BmhRX6sv7I7Oj5_iO0NudOvIHg9plw2---zcL0WOnpsX_TRczGBOk68QPzufvflC1FZQItPcNEEw7xL5imCcVRqCMv8BMGkSQamOXQJukcyBwjWu1jwWb1_gTNiA1WzkXdSArs4P5KvxVwR5aiV7-Q8e",
            fullnessPercentage = 15,
            isTrending = true
        ),
        Event(
            id = "3",
            title = "Open Air Kino",
            location = "Königsplatz",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDn_niMUwvoQJeWNAJmCC-h4-HCIP6qkNC6q2D54DCr4A5_w-2yGCk8hn0FdEJpx50Yo9OBBWdt5Q30JqKySuCbyWlv4FyynZq_qGDtEuSQPOcBdHLEGHwf2KCqXBO9h1D7MYhNOCAJGb-MCZUDsmbhyjUgOCEc5BF8vdv-1qRtNZId64Ku0-PXzNniica37wmAJlFAQF7w0t-lfqZQDaoTULTSm_1OGj_9Cx9e6O_u4U9naUJ7SKQ05-zNh-zh1Hoo1wrXN7W3dGqq",
            fullnessPercentage = 40
        ),
        Event(
            id = "4",
            title = "Street Food Market",
            location = "Werksviertel",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQJLTB34FdZDgETNFhkLJsLrQNOg-Ngjy_FQVqVDaDOOAfX_KH6vmACBk0orx0tEIr1nooiF7My5ROFmLTfYWnGZqCrJCHwaooCsjUoy0GnPsPwbQpn0kJUPJin_ZrkSGFcZ3Nm-bSeB7U-L3d5AURRVrYYE1EImgwm-I4UbsJQdBuT0b_XAB6YsS0guQ4Ybs3jTZsjgtRBGBcgWWrJgyuBgA9aQxSfWe88ZZ51UF9MftP6ECw0wdhY643uVAgtFAIvZdSZ2N6zzSz",
            fullnessPercentage = 75
        ),
        Event(
            id = "5",
            title = "The Lumineers",
            location = "Olympiahalle",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBOOv1m3Woq231e-WhVrHcG-u8L-_qhR7T5TTcWSIfK8VQGT-n0Q06rUx1nyIJEVF3nbWfx-bww0Y57g2iKovlw3_vHypDcF16D-O6LFkhGZXOjQVu-ZXU4m--F04vRzVsmGGzH6YkHa8vXC_Win3Ke4muZP2tKHd2PyK2_vU8UeLeqBx32Yod8b7bqXTXtG1iuKPuewMqK_3D3rFA28wlIzL76j-qdkVHx9TkMTfPs8hS3QoGxqBnHrP9jD2sKnYasoMbf4LSx1rHY",
            fullnessPercentage = 95
        ),
        Event(
            id = "6",
            title = "Kocherlball",
            location = "Englischer Garten",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCovLv2whviTCInAwuC0EufYELCYuyjxMkwoiuv6p7msdWCG3SU-_kvcYHr10WfqGvfZ88SjVdPBaIs77a1dibJ5UKkaLHGNmiLf4Qjc5aIDdi-f-DPolX6kywm1PUL7fzVi_NI36zipqeu82vdjuIUFQHjm1MmnJ4a4uzjbdzgr_05jMKquBKqEr7ru55B78av1F7FNONcZ_LittExNz1l9PuT5mpiFWgo_-TNQofQAiu7ZCLXS832tokc2hIWp3SJiq_wI_IAuAyw",
            fullnessPercentage = 60
        ),
        Event(
            id = "7",
            title = "Garbage Concert",
            location = "Zenith",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCX2T5qOOI-lKwwPMME3vlTEzdwxd7v6e51aIDOzNh3HYEK8PRaySdk9H9CgKmIj75_U3G8G64iLjRXyohCnyjmDn6JlwEbSMHj1-9jgvywEzHtQCxjNXw70a3Jl7lPAkBReyl93y9Yp_hyf3T7_j715bUlIRjq94156OKhHFpfFXZSJKq_eW6S_nSiNyH2exI0xxmxyARi87MeeqnyHgTO6BzqcI_Ukg2Q9quYAd5BW3ZH0WkYQXwSEXK9CXg7LmCHN1dVo5Zbogv9",
            fullnessPercentage = 30
        )
    )

    override fun getTrendingEvents(): Flow<List<Event>> = flowOf(events.filter { it.isTrending })
    override fun getNearbyEvents(): Flow<List<Event>> = flowOf(events.filter { !it.isTrending })
    override fun getAllEvents(): Flow<List<Event>> = flowOf(events)
}
