# Proguard rules that are applied to your test apk/code.
# JUnit
-dontnote org.junit.**
-dontnote junit.framework.**
-dontnote junit.runner.**

# Android
-dontwarn android.content.**
-dontnote androidx.test.**

# Google GSON
-dontnote com.google.gson.internal.**
