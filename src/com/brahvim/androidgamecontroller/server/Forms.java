package com.brahvim.androidgamecontroller.server;

import uibooster.model.FormBuilder;

public enum Forms {
    SETTINGS(new AgcForm() {
        public FormBuilder setup() {
            return null;
        };
    }),

    NEW_CONNECTION(new AgcForm() {
        public FormBuilder setup() {
            return null;
        };
    }),

    BANS(new AgcForm() {
        public FormBuilder setup() {
            return null;
        };
    }),

    UNBAN(new AgcForm() {
        public FormBuilder setup() {
            return null;
        };
    });

    private AgcForm form;

    // Apparently these are `private` by default.
    /* private */ Forms(AgcForm p_form) {
        this.form = p_form;
    }

    public AgcForm getInstance() {
        return this.form;
    }
}
