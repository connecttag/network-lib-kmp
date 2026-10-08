# ConnectTag Network Library (network-lib-kmp)

[![JitPack](https://jitpack.io/v/connecttag/network-lib-kmp.svg)](https://jitpack.io/#connecttag/network-lib-kmp)

مكتبة إدارة الشبكات والاتصال عبر Ktor متعددة المنصات (Kotlin Multiplatform):
- **Android** (API 24+)
- **Desktop JVM** (Windows, macOS, Linux)
- **iOS** (Arm64, SimulatorArm64)

---

## 📦 التثبيت والإضافة للمشروع (Installation via JitPack)

### 1. إضافة مستودع JitPack في `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. إضافة التبعية في `build.gradle.kts`:
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            // For Kotlin Multiplatform:
            implementation("com.github.connecttag.network-lib-kmp:network-lib-kmp:1.0.0")
        }
    }
}
```

الحزمة الأساسية للمكتبة: `org.connecttag.lib.network.*`

---

## 🌟 الميزات الأساسية

1. **`AppHttpClientFactory`:**
   - بناء عميل `HttpClient` لـ Ktor مهيأ مسبقاً بميزات Structured Telemetry، وإعادة المحاولة الذكية (Exponential Backoff Retries للخطأ 5xx)، والمهلات (Timeouts)، والـ Content Negotiation مع `kotlinx.serialization`.
2. **`TokenRefreshCoordinator`:**
   - معالجة تلقائية منسقة لتحديث التوكنات المنتهية (401/403) مع قفل تزامن (`Mutex`) لمنع استدعاءات التحديث المتعددة المتزامنة.
3. **`AuthTokenProvider` & `AuthTokenUseCases`:**
   - واجهات وعقود موحدة لتوفير وحفظ واسترجاع توكنات الجلسة والـ Bearer tokens عبر جميع المنصات.
4. **`ConnectivityService` & `ConnectivityObserver`:**
   - فحص دوري للاتصال والنبض (Heartbeat Ping) مع مراقبة حالة الات بالخادم والإنترنت وتوليد أحداث `StateFlow<ConnectivityState>`.
5. **`NetworkEventBus`:**
   - ناقل أحداث موحد (`SharedFlow`) لأحداث الشبكة العامة (Forbidden, Token Expired, Connection Lost).
6. **`ApiResponse` & Data Contracts:**
   - نماذج آمنة للنوع (`ApiResponse.Success`, `ApiResponse.Error`) مع كود الحالة ورسائل الخطأ القابلة للتعامل البرمجي.
