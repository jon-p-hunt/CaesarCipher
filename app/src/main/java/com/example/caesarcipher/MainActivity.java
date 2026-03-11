package com.example.caesarcipher;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * CaesarCipherApp — Android translation of the JavaFX original.
 *
 * JavaFX → Android mapping
 * ─────────────────────────────────────────────────────────────────
 * VBox / HBox              → LinearLayout (vertical / horizontal)
 * TextArea (input)         → EditText  (inputType="textMultiLine")
 * TextArea (output)        → EditText  (focusable=false)
 * Slider (0-25)            → SeekBar   (max=25)
 * RadioButton + ToggleGroup→ RadioButton inside RadioGroup
 * Clipboard.setContent()   → ClipboardManager.setPrimaryClip()
 * Platform.runLater()      → Handler(Looper.getMainLooper()).postDelayed()
 * textProperty listener    → TextWatcher
 * valueProperty listener   → SeekBar.OnSeekBarChangeListener
 * ─────────────────────────────────────────────────────────────────
 */
public class MainActivity extends AppCompatActivity {

    // ── Views ────────────────────────────────────────────────────────────────
    private EditText    etInput;
    private EditText    etOutput;
    private SeekBar     seekBarShift;
    private TextView    tvShiftValue;
    private RadioGroup  radioGroupMode;
    private Button      btnCopy;
    private Button      btnClear;

    // ── State ────────────────────────────────────────────────────────────────
    private int  currentShift = 3;
    private boolean processingLocked = false;   // prevent recursive updates

    // ── Handler for "Copied!" → revert button label after 1.5 s ─────────────
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ════════════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wire up views
        etInput       = findViewById(R.id.etInput);
        etOutput      = findViewById(R.id.etOutput);
        seekBarShift  = findViewById(R.id.seekBarShift);
        tvShiftValue  = findViewById(R.id.tvShiftValue);
        radioGroupMode= findViewById(R.id.radioGroupMode);
        btnCopy       = findViewById(R.id.btnCopy);
        btnClear      = findViewById(R.id.btnClear);

        // android:hintTextColor is not a valid XML attribute — must be set in code.
        // Uses the same #8D99AE muted slate colour matching the JavaFX prompt text.
        etInput.setHintTextColor(getResources().getColor(R.color.text_muted, getTheme()));

        // ── SeekBar → mirrors JavaFX Slider (0-25, initial value 3) ─────────
        seekBarShift.setProgress(currentShift);
        seekBarShift.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentShift = progress;
                tvShiftValue.setText(String.valueOf(progress));
                processText();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar)  {}
        });

        // ── RadioGroup → mirrors JavaFX RadioButton + ToggleGroup ────────────
        radioGroupMode.setOnCheckedChangeListener((group, checkedId) -> processText());

        // ── Input TextWatcher → mirrors JavaFX textProperty listener ─────────
        etInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged   (CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                processText();
            }
        });

        // ── Copy button → mirrors JavaFX Clipboard logic + visual feedback ───
        btnCopy.setOnClickListener(v -> {
            String output = etOutput.getText().toString();
            if (output.isEmpty()) {
                Toast.makeText(this, getString(R.string.nothing_to_copy), Toast.LENGTH_SHORT).show();
                return;
            }
            ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Caesar Cipher Output", output);
            clipboard.setPrimaryClip(clip);

            // Visual feedback — "✓ Copied!" for 1.5 s, then restore label
            btnCopy.setText(getString(R.string.copied));
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler.postDelayed(
                () -> btnCopy.setText(getString(R.string.copy_output)),
                1500
            );
        });

        // ── Clear button → mirrors JavaFX clear() calls ──────────────────────
        btnClear.setOnClickListener(v -> {
            processingLocked = true;        // suppress the TextWatcher firing mid-clear
            etInput.setText("");
            etOutput.setText("");
            processingLocked = false;
        });

        // Initial cipher pass so the output field isn't blank on launch
        processText();
    }

    // ── Core logic ───────────────────────────────────────────────────────────

    /**
     * Reads the current UI state and updates the output field.
     * Direct Android equivalent of the JavaFX processText() method.
     */
    private void processText() {
        if (processingLocked) return;

        String input   = etInput.getText().toString();
        int    shift   = currentShift;
        boolean encrypt = (radioGroupMode.getCheckedRadioButtonId() == R.id.radioEncrypt);

        String result = caesarCipher(input, shift, encrypt);

        processingLocked = true;
        etOutput.setText(result);
        processingLocked = false;
    }

    /**
     * Caesar cipher implementation — identical logic to the JavaFX original.
     *
     * @param text    Plain or cipher text to process.
     * @param shift   Shift value (0–25).
     * @param encrypt {@code true} to encrypt, {@code false} to decrypt.
     * @return        Transformed text (non-alpha characters pass through unchanged).
     */
    private String caesarCipher(String text, int shift, boolean encrypt) {
        if (!encrypt) {
            shift = 26 - shift;     // reverse shift for decryption — same as JavaFX
        }

        StringBuilder result = new StringBuilder(text.length());

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base   = Character.isUpperCase(c) ? 'A' : 'a';
                int  offset = (c - base + shift) % 26;
                result.append((char) (base + offset));
            } else {
                result.append(c);   // spaces, digits, punctuation pass through
            }
        }

        return result.toString();
    }
}
