const net = require('node:net');
const { spawn } = require('node:child_process');

// El backend autoriza los orígenes de desarrollo del puerto 4200.
// CI evita que Angular ofrezca un puerto alternativo si aparece una carrera.
const host = '127.0.0.1';
const port = 4200;
const probe = net.createServer();
probe.once('error', (error) => {
  if (error.code === 'EADDRINUSE') {
    console.error('\nEl puerto 4200 ya está ocupado.');
    console.error('Si TuDentaria ya está abierta, usa http://127.0.0.1:4200/login');
    console.error('Para reiniciarla, detén la terminal anterior con Ctrl+C y vuelve a ejecutar npm start.');
    console.error('No se iniciará Angular en otro puerto porque el backend lo rechazaría.\n');
  } else {
    console.error(`No se pudo usar ${host}:${port}: ${error.message}`);
  }
  process.exitCode = 1;
});
probe.listen(port, host, () => {
  probe.close(() => {
    const child = spawn(process.execPath, [
      require.resolve('@angular/cli/bin/ng.js'), 'serve',
      '--host', host, '--port', String(port),
      '--proxy-config', 'proxy.conf.cjs', '--poll', '2000',
    ], { stdio: 'inherit', env: { ...process.env, CI: 'true' } });
    child.once('error', (error) => {
      console.error(`No se pudo iniciar Angular: ${error.message}`);
      process.exitCode = 1;
    });
    child.once('exit', (code) => { process.exitCode = code ?? 1; });
    for (const signal of ['SIGINT', 'SIGTERM']) {
      process.on(signal, () => child.kill(signal));
    }
  });
});
