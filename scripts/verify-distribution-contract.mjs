import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';

const contract = JSON.parse(
  readFileSync(new URL('../.zargard/distribution-contract.json', import.meta.url)),
);
const build = readFileSync(
  new URL('../app/build.gradle.kts', import.meta.url),
  'utf8',
);
const ui = readFileSync(
  new URL(
    '../app/src/main/java/com/pouriaquant/goldarb/ui/GoldArbApp.kt',
    import.meta.url,
  ),
  'utf8',
);

const fa = (value) =>
  String(value).replace(/[0-9]/g, (digit) => '۰۱۲۳۴۵۶۷۸۹'[Number(digit)]);
const faVersion = (value) => fa(value).replaceAll('.', '٫');

assert.match(
  build,
  new RegExp(`versionName = "${contract.distribution.androidVersion}"`),
);
assert.ok(ui.includes(`MetricCard("منابع", "${fa(contract.coverage.catalogTotal)}"`));
assert.ok(ui.includes('MetricCard("ناموفق"'));
assert.ok(ui.includes(`CoverageBucket("${fa(contract.coverage.catalogTotal)}", "منابع بررسی‌شده"`));
assert.ok(ui.includes(`نسخه اندروید ${faVersion(contract.distribution.androidVersion)}`));
assert.equal(contract.strategy.activePath, 'prefunded-cross-venue-inventory');
assert.equal(contract.strategy.minimumNetProfitScope, 'per-order');
assert.equal(contract.strategy.minimumNetProfitRate, 0.05);
assert.equal(contract.strategy.minimumNetProfitBasis, 'lower-leg-notional');
assert.equal(contract.strategy.initialTomanBalancePerVenue, 50_000_000);
assert.deepEqual(contract.strategy.screeningQuantitiesGram, [0.2, 1, 5, 10]);
assert.equal(contract.strategy.requiresDirectBidAsk, true);
assert.equal(contract.strategy.requiresDirectionReversal, true);
assert.equal(contract.strategy.initialScreeningHours, 72);
assert.equal(contract.strategy.tokenizedGoldStatus, 'paused');
assert.ok(ui.includes('زرگرد سفارشی ارسال نمی‌کند'));

console.log('Android distribution contract is consistent.');
