# 🚀 دليل تشغيل موقع أحمد حلوة — خطوة بخطوة

الموقع جاهز بالكامل (عربي + إنجليزي). فاضل 3 خطوات بسيطة تعملها بنفسك:

---

## 1️⃣ ربط الفورم بـ Google Sheets (5 دقايق)

1. افتح [Google Sheets](https://sheets.google.com) واعمل شيت جديد، سمّيه مثلًا **"عملاء الموقع"**.
2. من القائمة: **Extensions ← Apps Script**.
3. امسح أي كود موجود، وانسخ مكانه كل محتوى الملف [`apps-script/Code.gs`](apps-script/Code.gs).
4. اضغط **Deploy ← New deployment**.
5. اختار النوع: **Web app**، وظبّط:
   - **Execute as:** `Me`
   - **Who has access:** `Anyone` ← مهم جدًا
6. اضغط **Deploy** ووافق على الصلاحيات (هيقولك التطبيق غير مُتحقق — اضغط Advanced ← Go to project).
7. انسخ اللينك اللي هيظهر (بيبدأ بـ `https://script.google.com/macros/s/...`).
8. افتح الملف `assets/js/form.js` وحط اللينك مكان `PASTE_YOUR_APPS_SCRIPT_URL_HERE` في أول سطر.

✅ من اللحظة دي، أي عميل يسجل في الفورم هتنزل بياناته صف جديد في الشيت فورًا.

---

## 2️⃣ رفع الصور

| الصورة | المكان | ملاحظات |
|---|---|---|
| صورتك الشخصية | `assets/img/ahmed.jpg` | صورة احترافية، الأفضل بأبعاد طولية 4:5 |
| سكرينشوت حملة 1 (681 شراء) | `assets/img/results/case-1.jpg` | |
| سكرينشوت حملة 2 (134 شراء) | `assets/img/results/case-2.jpg` | |
| سكرينشوت حملة 3 (145 محادثة) | `assets/img/results/case-3.jpg` | |
| سكرينشوت حملة 4 (313 محادثة) | `assets/img/results/case-4.jpg` | |

> لو أي صورة مش موجودة الموقع بيتعامل تلقائيًا — مش هيظهر مكان فاضي مكسور.

---

## 3️⃣ تفعيل GitHub Pages (مجاني)

1. ارفع محتويات الفولدر ده على ريبو GitHub (الملفات دي تكون في الـ root مش جوه فولدر).
2. من صفحة الريبو: **Settings ← Pages**.
3. تحت **Build and deployment**:
   - **Source:** `Deploy from a branch`
   - **Branch:** `main` — Folder: `/ (root)`
4. اضغط **Save** واستنى دقيقة.
5. الموقع هيشتغل على: `https://USERNAME.github.io/REPO-NAME/`

> 💡 لو هتشتري دومين خاص (مثلًا ahmedhelwa.com) تقدر تربطه من نفس صفحة Pages ← Custom domain.

---

## 🗂 هيكل الملفات

```
├── index.html          ← الصفحة الرئيسية (عربي)
├── results.html        ← صفحة النتائج (عربي)
├── en/
│   ├── index.html      ← الرئيسية (إنجليزي)
│   └── results.html    ← النتائج (إنجليزي)
├── assets/
│   ├── css/style.css   ← التصميم والهوية كاملة
│   ├── js/main.js      ← الحركات والأنيميشن
│   ├── js/form.js      ← إرسال الفورم لجوجل شيت (⚠️ حط اللينك هنا)
│   └── img/            ← الصور (صورتك + السكرينشوتات)
└── apps-script/Code.gs ← كود جوجل شيت (تنسخه في Apps Script)
```
