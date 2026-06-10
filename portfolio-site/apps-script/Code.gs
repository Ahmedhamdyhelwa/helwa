/**
 * Ahmed Helwa — Lead form receiver
 * يستقبل بيانات الفورم من الموقع ويسجلها صف جديد في Google Sheet.
 *
 * خطوات التشغيل موجودة في SETUP.md
 */

const SHEET_NAME = 'Leads';

const COLUMNS = [
  ['submitted_at', 'تاريخ التسجيل'],
  ['lang',         'لغة الموقع'],
  ['brand_name',   'اسم البراند'],
  ['owner_name',   'اسم صاحب البراند'],
  ['whatsapp',     'واتساب'],
  ['email',        'إيميل'],
  ['social_links', 'لينكات السوشيال'],
  ['product_type', 'نوع المنتج'],
  ['country',      'الدولة المستهدفة'],
  ['budget',       'الميزانية الشهرية'],
  ['currency',     'العملة'],
  ['services',     'الخدمات المطلوبة'],
  ['notes',        'تفاصيل إضافية'],
];

function doPost(e) {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  let sheet = ss.getSheetByName(SHEET_NAME);

  if (!sheet) {
    sheet = ss.insertSheet(SHEET_NAME);
    sheet.appendRow(COLUMNS.map(c => c[1]));
    sheet.getRange(1, 1, 1, COLUMNS.length).setFontWeight('bold').setBackground('#0f1729').setFontColor('#22d3ee');
    sheet.setFrozenRows(1);
  }

  const data = JSON.parse(e.postData.contents);
  sheet.appendRow(COLUMNS.map(c => data[c[0]] || ''));

  return ContentService
    .createTextOutput(JSON.stringify({ ok: true }))
    .setMimeType(ContentService.MimeType.JSON);
}
