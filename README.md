# 🛒 Hepsiburada E-Commerce Test Automation Framework

![Java 17](https://img.shields.io/badge/Java-17-orange.svg)
![Gauge Framework](https://img.shields.io/badge/Gauge-1.6+-blue.svg)
![Selenium WebDriver](https://img.shields.io/badge/Selenium-4.18.1-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)
![Architecture](https://img.shields.io/badge/Architecture-POM%20%2B%20JSON%20Repository-purple.svg)

Bu proje, **Hepsiburada** e-ticaret platformunda uçtan uca (End-to-End) kritik satın alma akışını otomatize etmek ve doğrulamak amacıyla geliştirilmiş, kurumsal standartlarda **Java 17**, **ThoughtWorks Gauge** ve **Page Object Model (POM)** tabanlı bir test otomasyon framework'üdür.

---

## 📋 Uçtan Uca Test Akışı (E2E Scenario Flow)

Otomasyon senaryosu (`specs/Scenarios/Shopping/shopping_cart.spec`), gerçek bir kullanıcının satın alma yolculuğunu aşağıdaki sıra ile yürütür:

1. **Anasayfa Erişimi:** Hepsiburada ana sayfası açılır, varsa çerez izin bildirimleri kapatılır.
2. **Kullanıcı Girişi (Login):** Kullanıcı giriş sayfasına yönlendirilir; `value-infos/users.json` içerisindeki tanımlı kullanıcı bilgileriyle oturum açılır ve ana sayfada kullanıcı adının görüntülendiği doğrulanır.
3. **Ürün Arama:** Arama kutusu tetiklenerek `"bilgisayar"` kelimesi aratılır ve arama sonuç sayfasının yüklendiği doğrulanır.
4. **Dinamik Ürün Seçimi (2. Satır 1. Ürün):** Arama sonuçları grid listesinden **2. satır 1. ürün** dinamik olarak hesaplanarak seçilir, ürün başlığı hafızaya (`DataStore`) alınır ve yeni sekmede açılan ürün detayına geçilir.
5. **Sepete Ekleme:** Ürün detay sayfasındaki "Sepete Ekle" butonuna tıklanır ve işlemin başarılı olduğunu belirten onay modalı/toast bildirimi doğrulanır.
6. **Sepet Doğrulaması:** Sepetim sayfasına gidilir; arama listesinde seçilen ve hafızaya kaydedilen ürün adının sepetteki ürünle birebir eşleştiği `AssertJ` ile doğrulanır.

---

## 🔍 2. Satır 1. Ürünün Dinamik Seçim Mekanizması

### Karşılaşılan Problem
E-ticaret arayüzleri responsive (duyarlı) CSS mimarisine sahiptir. Ekran çözünürlüğüne, pencere boyutuna veya cihaz tipine göre arama listesinde bir satırdaki kolon (ürün) sayısı **3, 4 veya 5** olarak değişkenlik gösterir:
* **Statik İndeks Kırılganlığı:** Klasik yaklaşımda 4 kolonlu bir ekranda 2. satırın ilk elemanı `products.get(4)` (5. ürün) olarak kodlanır. Ancak tarayıcı penceresi küçüldüğünde satır başına 3 ürün düştüğü senaryoda `products.get(4)` 2. satırın 1. elemanını değil, 2. elemanını seçer; bu durum testin güvenilirliğini bozar.

### Uygulanan Mühendislik Çözümü (CSS Grid Template Columns)
Hepsiburada ürün listesi modern **CSS Grid** (`display: grid`) yapısıyla oluşturulmuştur. Bu doğrultuda çözüm $O(1)$ çalışma zamanı karmaşıklığı ile şu adımlarla tasarlanmıştır:

1. **Grid Kuralının Dinamik Okunması:**
   Grid kapsayıcısı (`productListContainer`) üzerinden tarayıcının render ettiği hesaplanmış sütun kuralı JavaScript yardımıyla okunur:
   ```javascript
   window.getComputedStyle(container).gridTemplateColumns
   ```
2. **Kolon Sayısının Belirlenmesi:**
   Tarayıcının döndürdüğü piksel değerleri (örn. `"230px 230px 230px 230px"` $\rightarrow$ 4 kolon, küçük pencerede `"260px 260px 260px"` $\rightarrow$ 3 kolon) ayrılarak satır başına düşen anlık `columnCount` dinamik olarak tespit edilir.
3. **Matematiksel İndeksleme:**
   Grid üzerindeki herhangi bir satır ve sütundaki elemanın indeksi formülle doğrudan bulunur:
   $$\text{targetIndex} = (\text{targetRow} - 1) \times \text{columnCount} + (\text{targetCol} - 1)$$
   * 4 kolonlu ekranda: $(2 - 1) \times 4 + (1 - 1) = \mathbf{4}$ (İndeks 4)
   * 3 kolonlu küçük ekranda: $(2 - 1) \times 3 + (1 - 1) = \mathbf{3}$ (İndeks 3)
4. **Doğru Bağlantının Tetiklenmesi:**
   Dış kart çerçevesi yerine kart içerisindeki gerçek yönlendirme bağlantısı (`productCardLink` $\rightarrow$ `<a>`) hedeflenir. Böylece kartın boş alanlarına veya diğer aksiyon butonlarına çarpmadan doğrudan ürün detayının yeni sekmede açılması sağlanır.
5. **Dinamik Sekme Yönetimi:**
   `BasePage.switchToLastTab()` metodu, `wait.until(d -> d.getWindowHandles().size() > 1)` explicit wait kuralı ile yeni pencere tanıtıcısı oluşana kadar bekler ve yeni sekmeye pürüzsüz geçiş yapar.

---

## 🏗 Mimari Özellikler

* **Page Object Model (POM):** UI etkileşimleri ve iş mantığı `com.vrgsl.step.pages` paketindeki sayfa sınıflarında toplanmıştır.
* **Merkezi & Modüler JSON Element Repository:** Tüm element lokatörleri `src/test/resources/element-infos/` altında modüler klasörlerde (`Home`, `Login`, `Shopping`) tutulur. Her element tekil, net ve no-fallback (`|` içermeyen) yapıda tanımlanmıştır.
* **Modüler Spec & Concept Mimarisi:** Test senaryoları ve konseptler domain bazlı klasör yapısında (`specs/Scenarios/Shopping/`, `specs/Concepts/Login/`) organize edilmiştir.
* **Thread-Safe Sürücü Yönetimi:** `DriverManager.java` sınıfı `ThreadLocal<WebDriver>` mimarisiyle izole bir sürücü yaşam döngüsü sunar.
* **Senaryolar Arası Veri İletişimi:** Seçilen ürün başlığı Gauge'in `Scenario DataStore` yapısı (`StoreHelper.java`) üzerinde taşınarak adımlar arasında veri sürekliliği sağlanır.

### Dizin Yapısı
```text
qa-virgosol-hb/
├── specs/
│   ├── Concepts/
│   │   └── Login/
│   │       └── login_page.cpt           # Yeniden kullanılabilir oturum açma konseptleri
│   └── Scenarios/
│       └── Shopping/
│           └── shopping_cart.spec       # Uçtan uca ürün arama ve sepet senaryosu
├── src/test/java/com/vrgsl/step/
│   ├── driver/                          # WebDriver ve Hook sınıfları
│   ├── helper/                          # Konfigürasyon, element ve veri yardımcıları
│   ├── pages/                           # Modüler Page Object sınıfları
│   │   ├── BasePage.java                # Temel sayfa sınıfı ve dinamik Grid mantığı
│   │   ├── home/
│   │   │   └── HomePage.java
│   │   ├── login/
│   │   │   └── LoginPage.java
│   │   └── shopping/
│   │       ├── CartPage.java
│   │       ├── ProductDetailPage.java
│   │       └── SearchResultsPage.java
│   └── steps/                           # Gauge adım implementasyonları
└── src/test/resources/
    ├── element-infos/                   # Modüler tekil JSON lokatör katalogları
    │   ├── Home/
    │   │   └── home.json
    │   ├── Login/
    │   │   └── login.json
    │   └── Shopping/
    │       ├── cart.json
    │       ├── product_detail.json
    │       └── search.json
    ├── value-infos/
    │   └── users.json                   # Test kullanıcı bilgileri ve arama anahtarları
    ├── browser-options.yaml             # Tarayıcı çalıştırma parametreleri
    └── qa-web.yaml                      # Ortam URL konfigürasyonları
```

---

## 🚀 Kurulum ve Çalıştırma

### Ön Koşullar
* **Java:** JDK 17+
* **Maven:** 3.8+
* **Google Chrome:** Güncel sürüm
* **Gauge CLI:** [Resmi dokümantasyona](https://docs.gauge.org/getting_started/installing-gauge.html) göre kurulu olmalıdır.

### Testleri Koşturma

```bash
# Projeyi derleme
mvn clean test-compile

# Test senaryosunu çalıştırma
gauge run specs

# veya Maven üzerinden çalıştırma
mvn test
```

### Raporlama
Test tamamlandığında zengin içerikli Gauge HTML raporu otomatik olarak oluşturulur:
* Rapor konumu: `reports/html-report/index.html`
* Raporu tarayıcıda açmak için (Mac):
  ```bash
  open reports/html-report/index.html
  ```
