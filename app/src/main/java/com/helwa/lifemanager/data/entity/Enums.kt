package com.helwa.lifemanager.data.entity

/** أولوية المهمة */
enum class Priority(val arabic: String) {
    HIGH("عالية"),
    MEDIUM("متوسطة"),
    LOW("منخفضة")
}

/** نوع تكرار المهمة */
enum class RepeatType(val arabic: String) {
    ONCE("مرة واحدة"),
    DAILY("يومي"),
    WEEKLY("أسبوعي")
}

/** فئة المهمة */
enum class Category(val arabic: String) {
    PERSONAL("شخصية"),
    WORK("عمل"),
    HEALTH("صحة"),
    SHOPPING("تسوق"),
    OTHER("أخرى")
}
