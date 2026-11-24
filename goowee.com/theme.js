/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
(() => {
    const root = document.documentElement;
    const buttons = [...document.querySelectorAll('[data-theme-choice]')];
    const systemPreference = window.matchMedia('(prefers-color-scheme: dark)');
    const mobileViewport = window.matchMedia('(max-width: 760px)');

    let choice = root.dataset.themeChoice || 'system';

    const applyTheme = (nextChoice, persist = true) => {
        choice = nextChoice;
        const useSystemTheme = mobileViewport.matches || choice === 'system';
        const resolvedTheme = useSystemTheme ? (systemPreference.matches ? 'dark' : 'light') : choice;

        root.dataset.themeChoice = choice;
        root.dataset.theme = resolvedTheme;

        for (const button of buttons) {
            button.setAttribute('aria-pressed', String(button.dataset.themeChoice === choice));
        }

        if (persist) {
            try {
                localStorage.setItem('goowee-theme', choice);
            } catch (error) {
                // The selected theme remains active until the page is closed.
            }
        }
    };

    for (const button of buttons) {
        button.addEventListener('click', () => applyTheme(button.dataset.themeChoice));
    }

    systemPreference.addEventListener('change', () => {
        if (choice === 'system' || mobileViewport.matches) {
            applyTheme(choice, false);
        }
    });

    mobileViewport.addEventListener('change', () => applyTheme(choice, false));

    applyTheme(choice, false);
})();
