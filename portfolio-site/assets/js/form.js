// Lead form → Google Sheets (via Google Apps Script Web App)
// ⚠️ بعد ما تعمل خطوات الربط في SETUP.md، حط لينك الـ Web App هنا:
const APPS_SCRIPT_URL = 'PASTE_YOUR_APPS_SCRIPT_URL_HERE';

const form = document.getElementById('leadForm');
const msgBox = document.getElementById('formMsg');
const submitBtn = document.getElementById('submitBtn');
const isEnglish = document.body.classList.contains('en');

const TEXT = isEnglish
  ? {
      sending: '⏳ Sending...',
      send: '🚀 Send Your Details',
      ok: '✅ Received! I\'ll get back to you very soon on WhatsApp.',
      missing: '⚠️ Please fill in all required fields and pick at least one service.',
      err: '⚠️ Something went wrong. Please try again or message me directly on social media.',
      notConfigured: '⚠️ The form isn\'t connected yet. Please reach me on Facebook or Instagram (links below).',
    }
  : {
      sending: '⏳ جاري الإرسال...',
      send: '🚀 ابعت بياناتك',
      ok: '✅ وصلتني بياناتك! هتواصل معاك في أقرب وقت على الواتساب.',
      missing: '⚠️ من فضلك كمّل كل الخانات المطلوبة واختار خدمة واحدة على الأقل.',
      err: '⚠️ حصلت مشكلة في الإرسال. جرب تاني أو كلمني مباشرة على السوشيال ميديا.',
      notConfigured: '⚠️ الفورم لسه مش متوصل. كلمني على فيسبوك أو انستجرام (اللينكات تحت).',
    };

function showMsg(type, text) {
  msgBox.className = 'form-msg ' + type;
  msgBox.textContent = text;
}

if (form) {
  form.addEventListener('submit', async e => {
    e.preventDefault();

    const services = [...form.querySelectorAll('input[name="services"]:checked')].map(c => c.value);
    const requiredOk = [...form.querySelectorAll('[required]')].every(f => f.value.trim() !== '');
    if (!requiredOk || services.length === 0) {
      showMsg('err', TEXT.missing);
      return;
    }

    if (APPS_SCRIPT_URL.startsWith('PASTE_')) {
      showMsg('err', TEXT.notConfigured);
      return;
    }

    const data = Object.fromEntries(new FormData(form).entries());
    data.services = services.join(' | ');
    data.lang = isEnglish ? 'EN' : 'AR';
    data.submitted_at = new Date().toISOString();

    submitBtn.disabled = true;
    submitBtn.textContent = TEXT.sending;
    try {
      // mode: no-cors — Apps Script web apps don't return CORS headers,
      // the request still reaches the sheet
      await fetch(APPS_SCRIPT_URL, {
        method: 'POST',
        mode: 'no-cors',
        headers: { 'Content-Type': 'text/plain;charset=utf-8' },
        body: JSON.stringify(data),
      });
      showMsg('ok', TEXT.ok);
      form.reset();
    } catch (err) {
      showMsg('err', TEXT.err);
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = TEXT.send;
    }
  });
}
