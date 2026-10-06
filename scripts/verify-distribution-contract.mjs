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

assert.match(
  build,
  new RegExp(`versionName = "${contract.distribution.androidVersion}"`),
);
assert.ok(ui.includes('ModalNavigationDrawer('));
const funds = ui.slice(ui.indexOf('private fun FundPairsPage'), ui.indexOf('private fun FundPairsPage') + 6000);
assert.ok(!/\bNAV\b/.test(funds), 'fund view must not restore the removed NAV label');
assert.ok(funds.includes('قیمت خرید هر واحد (ریال)'));
assert.ok(funds.includes('قیمت فروش هر واحد (ریال)'));
assert.ok(ui.includes('"طلای ۱۸ عیار"'));
assert.ok(ui.includes('"نقره ۹۹۹"'));
assert.ok(ui.includes('"مس کاتد"'));
assert.ok(ui.includes('"USDT / تومان"'));
assert.ok(ui.includes('"دفتر فرضی سرور"'));
assert.ok(ui.includes('"بدون سفارش واقعی"'));
assert.equal(contract.strategy.activePath, 'prefunded-cross-venue-inventory');
assert.equal(contract.strategy.minimumNetProfitScope, 'per-market-per-order');
assert.equal(contract.strategy.minimumNetProfitRate, 0.005);
assert.equal(contract.strategy.minimumNetProfitBasis, 'lower-leg-notional');
assert.equal(contract.strategy.initialTomanBalancePerVenue, 50_000_000);
assert.equal(contract.strategy.inventoryStateVersion, 2);
assert.equal(contract.strategy.sizingMode, 'position-aware-dynamic');
assert.deepEqual(contract.strategy.screeningQuantitiesGram, [0.2, 1, 5, 10]);
assert.equal(contract.strategy.requiresDirectBidAsk, true);
assert.equal(contract.strategy.requiresDirectionReversal, true);
assert.equal(contract.strategy.initialScreeningHours, 72);
assert.equal(contract.strategy.tokenizedGoldStatus, 'paused');
assert.deepEqual(contract.multiAsset.markets, ['gold', 'silver', 'copper', 'usdt']);
assert.equal(contract.multiAsset.monitorIntervalMinutes, 1);
assert.equal(contract.multiAsset.usdtInitialTomanPerVenue, 100_000_000);
assert.equal(contract.multiAsset.usdtInitialAssetPerVenue, 200);
assert.equal(contract.multiAsset.tradeMode, 'server-paper-ledger');
assert.equal(contract.identity.visibleInClients, false);
  assert.equal(contract.schemaVersion, 13);
assert.equal(contract.managementPresentation.profitMetric, 'closed-trade-realized-signed');
assert.equal(contract.managementPresentation.negativeValuesVisible, true);
assert.equal(contract.managementPresentation.rawLedgerPreserved, true);

console.log('Android distribution contract is consistent.');
