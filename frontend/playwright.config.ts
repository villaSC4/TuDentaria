import { defineConfig } from '@playwright/test';
export default defineConfig({ testDir:'./e2e', fullyParallel:false, workers:1, timeout:45000,
  use:{baseURL:process.env.FRONTEND_URL || 'http://127.0.0.1:4200', viewport:{width:1440,height:1000}, screenshot:'only-on-failure', trace:'retain-on-failure'},
  reporter:[['list']],
});
