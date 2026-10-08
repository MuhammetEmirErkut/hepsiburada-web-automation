# 🛒 Hepsiburada E-Commerce Test Automation Framework

![Java 17](https://img.shields.io/badge/Java-17-orange.svg)
![Gauge Framework](https://img.shields.io/badge/Gauge-1.6.38-blue.svg)
![Selenium WebDriver](https://img.shields.io/badge/Selenium-4.18.1-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)
![Architecture](https://img.shields.io/badge/Architecture-POM%20%2B%20JSON%20Repository-purple.svg)

Bu proje, **Hepsiburada** e-ticaret platformunun uçtan uca (End-to-End) kritik kullanıcı yolculuklarını test etmek amacıyla geliştirilmiş, kurumsal standartlarda **Java**, **Gauge Framework** ve **Page Object Model (POM)** tabanlı bir test otomasyon projesidir.

---

## 📌 İçindekiler
- [Proje Özeti](#-proje-özeti)
- [Mimari ve Tasarım Desenleri](#-mimari-ve-tasarım-desenleri)
- [Kullanılan Teknolojiler](#-kullanılan-teknolojiler)
- [Proje Dizin Yapısı](#-proje-dizin-yapısı)
- [Ön Koşullar ve Kurulum](#-ön-koşullar-ve-kurulum)
- [Testleri Çalıştırma](#-testleri-çalıştırma)
- [Test Raporları ve Hata Ekran Görüntüleri](#-test-raporları-ve-hata-ekran-görüntüleri)
- [Senaryo Detayları (HB-TC01)](#-senaryo-detayları-hb-tc01)

---

## 🎯 Proje Özeti

Proje, Hepsiburada web sitesi üzerinde aşağıdaki adımları uçtan uca otomatik olarak yürütür ve doğrular:
1. **Giriş Yapma:** Güvenli oturum açma (Anti-Bot CDP Stealth desteğiyle).
2. **Arama:** Belirlenen anahtar kelimeyi (`bilgisayar`) arama ve sonuçları listeleme.
3. **Ürün Seçimi:** Grid üzerindeki 2. satır 1. ürünü dinamik olarak bularak detay sayfasına geçme.
4. **Sepete Ekleme:** Ürünü sepete ekleyip onay bildirimini (toast/modal) doğrulama.
5. **Sepet Doğrulaması:** Sepete giderek eklenen ürün adının sepetteki ürünle birebir eşleştiğini assert etme.

---

## 🏗 Mimari ve Tasarım Desenleri

* **Page Object Model (POM):** Her web sayfası (`HomePage`, `LoginPage`, `SearchResultsPage`, `ProductDetailPage`, `CartPage`) bağımsız Java sınıfları olarak modellenmiştir. Sayfa etkileşimleri adım sınıflarından izole edilmiştir.
* **JSON Element Repository:** Tüm XPath ve CSS seçicileri Java kodundan çıkarılarak `src/test/resources/element-infos/` altındaki JSON kataloglarında toplanmıştır.
* **Merkezi Değer Deposu (Value-Infos):** E-posta, şifre ve anahtar kelimeler `src/test/resources/value-infos/users.json` içerisinden dinamik olarak çözülür.
* **ThreadLocal WebDriver:** `DriverManager` ile paralel test koşumuna tam uyumlu, thread-safe bir sürücü altyapısı sağlanmıştır.
* **Anti-Bot & CDP (Chrome DevTools Protocol) Stealth:** Akamai ve bot algılama sistemlerini aşmak için gerçekçi kullanıcı fare ve klavye etkileşimi (`CdpHelper.java`) ve JavaScript stealth enjeksiyonu uygulanmıştır.
* **Çoklu Dil (Dual-Language BDD):** Gauge adımları hem **Türkçe** hem **İngilizce** etiketleri (`@Step({"...", "..."})`) destekler.

---

## 💻 Kullanılan Teknolojiler

| Bileşen | Teknoloji / Kütüphane | Versiyon | Açıklama |
|---|---|---|---|
| **Programlama Dili** | Java (OpenJDK) | 17 LTS | Temel test geliştirme dili |
| **BDD & Test Runner** | ThoughtWorks Gauge | 0.8.1 / 1.6+ | BDD spesifikasyon ve senaryo motoru |
| **Tarayıcı Otomasyonu** | Selenium Java | 4.18.1 | Web element etkileşim kütüphanesi |
| **Sürücü Yöneticisi** | WebDriverManager | 5.7.0 | Otomatik ChromeDriver/GeckoDriver kurulumu |
| **JSON Ayrıştırıcı** | Google Gson | 2.10.1 | `element-infos` ve `value-infos` JSON okuyucu |
| **YAML Ayrıştırıcı** | SnakeYAML | 2.7 | `browser-options.yaml` ve `qa-web.yaml` yöneticisi |
| **Doğrulama (Assertions)** | AssertJ Core | 3.25.3 | Akıcı ve açıklayıcı test assertion kütüphanesi |
| **Derleme Aracı** | Apache Maven | 3.9+ | Bağımlılık ve proje yaşam döngüsü yöneticisi |

---

## 📁 Proje Dizin Yapısı

```text
qa-virgosol-hb/
├── specs/
│   ├── concepts/
│   │   └── login.cpt                    # Yeniden kullanılabilir BDD giriş konseptleri
│   └── hepsiburada_shopping.spec        # HB-TC01 E2E Test Senaryosu (EN & TR)
├── src/
│   └── test/
│       ├── java/
│       │   └── com/vrgsl/step/
│       │       ├── driver/
│       │       │   ├── DriverManager.java       # ThreadLocal WebDriver yaşam döngüsü
│       │       │   └── DriverHooks.java         # @BeforeScenario / @AfterScenario & Screenshot
│       │       ├── helper/
│       │       │   ├── ConfigHelper.java        # YAML konfigürasyon yöneticisi
│       │       │   ├── ElementHelper.java       # Dinamik JSON element çözücü
│       │       │   ├── ValueHelper.java         # users.json veri çözücü
│       │       │   ├── StoreHelper.java         # Gauge DataStore (Hafıza yönetimi)
│       │       │   └── CdpHelper.java           # Anti-Bot insan benzeri etkileşim
│       │       ├── model/
│       │       │   └── ElementInfo.java         # Element JSON veri modeli
│       │       ├── pages/                       # Page Object Model (POM) sınıfları
│       │       │   ├── BasePage.java            # Ortak sayfa metotları
│       │       │   ├── HomePage.java            # Anasayfa ve arama aksiyonları
│       │       │   ├── LoginPage.java           # Giriş formları
│       │       │   ├── SearchResultsPage.java   # Sonuç listesi & 2. satır ürün seçimi
│       │       │   ├── ProductDetailPage.java   # Detay sayfası & sepete ekleme
│       │       │   └── CartPage.java            # Sepet kontrolleri
│       │       └── steps/
│       │           ├── StepImplementation.java  # POM sınıflarını çalıştıran test adımları
│       │           └── BaseSteps.java           # Çekirdek Gauge adımları
│       └── resources/
│           ├── element-infos/                   # JSON Element Depoları
│           │   ├── home.json
│           │   ├── login.json
│           │   ├── search.json
│           │   ├── product_detail.json
│           │   └── cart.json
│           ├── value-infos/                     # Statik Test Verileri
│           │   └── users.json                   # Kullanıcı e-posta ve şifre tanımları
│           ├── browser-options.yaml             # Tarayıcı bayrakları ve Stealth JS
│           └── qa-web.yaml                      # Test ortam URL tanımları
├── .gitignore
├── manifest.json
├── pom.xml
└── README.md
```

---

## ⚙️ Ön Koşullar ve Kurulum

### 1. Sistem Gereksinimleri
* **Java Development Kit (JDK):** Versiyon 17 veya üzeri
* **Apache Maven:** Versiyon 3.8+
* **Google Chrome:** Güncel sürüm
* **Gauge CLI:** Kurulu olmalıdır.

### 2. Gauge CLI Kurulumu (Mac / Homebrew)
```bash
brew install gauge
gauge install java
gauge install html-report
gauge install screenshot
```

*(Windows / Linux için: https://docs.gauge.org/getting_started/installing-gauge.html)*

### 3. Bağımlılıkları Yükleme ve Derleme
Proje kök dizininde terminali açarak aşağıdaki komutu çalıştırın:
```bash
mvn clean test-compile
```

---

## 🚀 Testleri Çalıştırma

### 🔹 1. Tüm Testleri Çalıştırma
```bash
gauge run specs
```
*veya Maven üzerinden:*
```bash
mvn test
```

### 🔹 2. Dile Göre Etiketle Çalıştırma (Tags)
* **Yalnızca İngilizce Senaryo:**
  ```bash
  gauge run --tags "english" specs
  ```
* **Yalnızca Türkçe Senaryo:**
  ```bash
  gauge run --tags "turkish" specs
  ```

### 🔹 3. Headless (Arayüzsüz) Modda Çalıştırma
```bash
mvn test -Dchrome.headless=true
```

---

## 📊 Test Raporları ve Hata Ekran Görüntüleri

Testler tamamlandığında Gauge otomatik olarak zengin içerikli bir HTML rapor üretir:

* **Rapor Yolu:** `reports/html-report/index.html`
* Tarayıcıda açmak için:
  ```bash
  open reports/html-report/index.html   # Mac
  start reports/html-report/index.html  # Windows
  ```
* **Otomatik Screenshot:** Eğer bir adım başarısız olursa `DriverHooks.java` anlık ekran görüntüsü yakalayarak rapora doğrudan ekler.

---

## 🧪 Senaryo Detayları (HB-TC01)

```gauge
# Hepsiburada E-Commerce Test Suite

## HB-TC01 – Login, bilgisayar ara, ikinci satırdaki ilk ürünü sepete ekle ve sepette olduğunu doğrula
Tags: e2e, shopping_cart, smoke, p1, assignment

* Open Hepsiburada home page
* Navigate to login page
* Login as default user
* Verify that user is successfully logged in
* Search for product "searchKeyword"
* Verify search results are displayed
* Select the first product in the second row and navigate to product details
* Add product to cart and verify confirmation
* Navigate to shopping cart page
* Verify that the selected product is present in the cart
```
