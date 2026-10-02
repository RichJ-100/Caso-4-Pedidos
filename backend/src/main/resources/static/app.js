const state = { products: [], metrics: null };
const $ = (selector) => document.querySelector(selector);
const money = (value) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'USD' }).format(value);

async function request(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error(`La API respondio ${response.status}`);
  return response.json();
}

function showNotice(message, error = false) {
  const notice = $('#notice'); notice.textContent = message; notice.hidden = false;
  notice.style.background = error ? '#fff0ed' : '#eff8d9';
  notice.style.borderColor = error ? '#f3b4a6' : '#c5df91';
  window.setTimeout(() => { notice.hidden = true; }, 5000);
}

function renderSummary() {
  const counts = state.metrics?.pedidosPorEstado || {};
  const stats = [['PENDIENTE_PAGO', 'Pendientes'], ['PAGADO', 'Pagados'], ['DESPACHADO', 'Despachados'], ['EXPIRADO', 'Reservas vencidas']];
  $('#summary-stats').innerHTML = stats.map(([key, label]) => `<div class="stat-card"><p class="eyebrow">ESTADO</p><span class="value">${counts[key] || 0}</span><span class="label">${label}</span></div>`).join('');
  $('#summary-products').innerHTML = state.products.slice(0, 4).map((product) => `<div class="product-row"><div><strong>${product.nombre}</strong><small>${product.sku}</small></div><span class="stock">${product.disponible} disponibles</span><span class="price">${money(product.precio)}</span></div>`).join('');
}

function renderCatalog() {
  $('#catalog-grid').innerHTML = state.products.map((product) => `<article class="catalog-card"><div class="catalog-art">${product.sku.slice(0, 3)}</div><div class="catalog-info"><h3>${product.nombre}</h3><p>${product.sku} · Inventario controlado</p><div class="catalog-bottom"><span>${money(product.precio)}</span><span>${product.disponible} disponibles</span></div></div></article>`).join('');
  $('#order-product').innerHTML = state.products.map((product) => `<option value="${product.sku}">${product.nombre} (${product.disponible} disponibles)</option>`).join('');
}

function renderMetrics() {
  const counts = state.metrics?.pedidosPorEstado || {}; const max = Math.max(1, ...Object.values(counts));
  $('#state-bars').innerHTML = Object.entries(counts).map(([name, count]) => `<div class="bar-row"><span>${name.replaceAll('_', ' ')}</span><div class="bar-track"><div class="bar-fill" style="width:${count / max * 100}%"></div></div><b>${count}</b></div>`).join('');
  const values = [['pedidosCompletados', 'Pedidos completados'], ['cancelaciones', 'Cancelaciones'], ['erroresDePago', 'Errores de pago'], ['pagosReembolsados', 'Pagos reembolsados'], ['minutosPromedioCompraADespacho', 'Minutos promedio']];
  $('#metric-numbers').innerHTML = values.map(([key, label]) => `<div class="metric-number"><strong>${state.metrics[key] ?? 0}</strong><span>${label}</span></div>`).join('');
}

async function loadData() {
  try { state.products = await request('/api/productos'); state.metrics = await request('/api/metricas'); renderSummary(); renderCatalog(); renderMetrics(); }
  catch (error) { showNotice('No se pudieron cargar los datos de la API. Revisa que el backend este activo.', true); }
}

function navigate() {
  const page = location.hash.replace('#', '') || 'resumen'; const valid = ['resumen', 'catalogo', 'nuevo', 'metricas']; const selected = valid.includes(page) ? page : 'resumen';
  document.querySelectorAll('.view').forEach((view) => { view.hidden = view.dataset.page !== selected; });
  document.querySelectorAll('.nav a').forEach((link) => link.classList.toggle('active', link.dataset.view === selected));
  $('#page-title').textContent = { resumen: 'Resumen de pedidos', catalogo: 'Catalogo e inventario', nuevo: 'Nuevo pedido', metricas: 'Metricas de negocio' }[selected];
}

$('#order-form').addEventListener('submit', async (event) => {
  event.preventDefault(); const form = new FormData(event.target); const key = `web-${Date.now()}`;
  try { const order = await request('/api/pedidos', { method: 'POST', headers: { 'Content-Type': 'application/json', 'Idempotency-Key': key }, body: JSON.stringify({ clienteEmail: form.get('email'), canal: form.get('canal'), items: [{ sku: form.get('sku'), cantidad: Number(form.get('cantidad')) }] }) }); $('#order-result').innerHTML = `<div class="notice" style="background:#eff8d9;border-color:#c5df91">Pedido <strong>#${order.id}</strong> creado. Referencia de pago: <strong>${order.referenciaPago}</strong></div>`; event.target.reset(); await loadData(); }
  catch (error) { showNotice('No se pudo crear el pedido. Comprueba el stock y los datos.', true); }
});
$('#refresh').addEventListener('click', loadData); $('#catalog-refresh').addEventListener('click', loadData); $('#metrics-refresh').addEventListener('click', loadData); window.addEventListener('hashchange', navigate);
setInterval(() => { $('#clock').textContent = new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }); }, 1000);
navigate(); loadData();