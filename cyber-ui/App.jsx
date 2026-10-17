import React, { useState, useEffect } from 'react';
import './App.css';

/**
 * CYBER_NUTRITION_TRACKER // VERSION 1.0.0
 * Aesthetic: Retro-futuristic CRT / Cyberdeck
 */

const THEME = {
  bg: '#08090a',
  primary: '#00ff66',
  border: '#1b382b',
  glow: 'rgba(0, 255, 102, 0.2)',
  font: "'JetBrains Mono', 'Courier New', monospace"
};

const CyberPanel = ({ title, unit, children, className = "" }) => (
  <div className={`cyber-panel ${className}`} style={{
    border: `1px solid ${THEME.border}`,
    background: 'rgba(0, 15, 5, 0.6)',
    padding: '1.2rem',
    position: 'relative',
    marginBottom: '1rem',
    clipPath: 'polygon(0 0, 100% 0, 100% calc(100% - 10px), calc(100% - 10px) 100%, 0 100%)'
  }}>
    <div className="panel-header" style={{
      fontSize: '0.65rem',
      color: THEME.border,
      marginBottom: '0.8rem',
      letterSpacing: '1px'
    }}>
      {unit} // {title.toUpperCase()}
    </div>
    {children}
  </div>
);

const TerminalButton = ({ label, onClick, variant = 'primary', pulsing = false }) => (
  <button
    onClick={onClick}
    className={`terminal-button ${pulsing ? 'pulse-glow' : ''} ${variant}`}
  >
    {label}
  </button>
);

const ProgressBar = ({ current, target }) => {
  const percentage = Math.min((current / target) * 100, 100);
  const blocks = 20;
  const filledBlocks = Math.floor((percentage / 100) * blocks);

  return (
    <div className="progress-container">
      <div className="progress-label">
        <span>CONSUMPTION_METER</span>
        <span>{current} / {target} KCAL</span>
      </div>
      <div className="led-bar">
        {Array.from({ length: blocks }).map((_, i) => (
          <div
            key={i}
            className={`led-block ${i < filledBlocks ? 'active' : ''}`}
          />
        ))}
      </div>
    </div>
  );
};

export default function App() {
  const [stats, setStats] = useState({
    weight: 78.5,
    height: 182,
    age: 28,
    gender: 'M'
  });

  const [goal, setGoal] = useState('MAINTAIN');
  const [target, setTarget] = useState(2500);
  const [consumed, setConsumed] = useState(1420);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [diet, setDiet] = useState('BALANCED');

  const [intakeUnits, setIntakeUnits] = useState([
    { id: 'UNIT_01', label: 'BREAKFAST', items: [{ name: 'OATS_REPLICATED', kcal: 450 }] },
    { id: 'UNIT_02', label: 'LUNCH', items: [{ name: 'SYNTH_CHICKEN', kcal: 620 }, { name: 'GLUCO_BAR', kcal: 150 }] },
    { id: 'UNIT_03', label: 'SNACKS', items: [{ name: 'CAFFEINATED_INFUSION', kcal: 200 }] },
    { id: 'UNIT_04', label: 'DINNER', items: [] },
  ]);

  const handleGeminiParse = async (inputString) => {
    console.log("SENDING_TO_GEMINI:", inputString);
    // TODO: Implement actual Gemini API call
    // const response = await fetch('https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=YOUR_API_KEY', { ... });
    // const data = await response.json();
    // Expected JSON: { "items": [{ "name": string, "calories": number }] }

    // Mock response for now:
    return { items: [{ name: inputString.toUpperCase(), kcal: Math.floor(Math.random() * 500) }] };
  };

  const handleLogSubmit = async (e) => {
    if (e.key === 'Enter') {
      const parsed = await handleGeminiParse(e.target.value);
      // Update logic here...
      setDialogOpen(false);
    }
  };

  const delta = target - 2150;

  const handleAiRebalance = async () => {
    console.log("QUERYING_GEMINI_FOR_REBALANCE...");
    // Mock call to Gemini
    alert("AI SUGGESTION: 1. PROTEIN_CONCENTRATE (250 KCAL)\n2. LEAFY_MATTER_V2 (50 KCAL)");
  };

  return (
    <div className="app-container">
      {/* 1. METABOLIC_CORE */}
      <CyberPanel title="METABOLIC_CORE" unit="SYS_01">
        <div className="hardware-inputs">
          <div className="input-group">
            <label>WGT</label>
            <input type="text" value={stats.weight} readOnly />
          </div>
          <div className="input-group">
            <label>HGT</label>
            <input type="text" value={stats.height} readOnly />
          </div>
          <div className="input-group">
            <label>AGE</label>
            <input type="text" value={stats.age} readOnly />
          </div>
          <div className="input-group">
            <label>GEN</label>
            <input type="text" value={stats.gender} readOnly />
          </div>
        </div>

        <div className="goal-selector">
          <button className={goal === 'CUT' ? 'active' : ''} onClick={() => setGoal('CUT')}>[CUT // -DEFICIT]</button>
          <button className={goal === 'MAINTAIN' ? 'active' : ''} onClick={() => setGoal('MAINTAIN')}>[MAINTAIN // 0]</button>
          <button className={goal === 'BULK' ? 'active' : ''} onClick={() => setGoal('BULK')}>[BULK // +SURPLUS]</button>
        </div>

        <div className="slider-control">
          <div className="slider-header">
            <label>TARGET_CALORIES</label>
            <span className="delta-readout">DELTA: {delta > 0 ? '+' : ''}{delta} KCAL</span>
          </div>
          <input
            type="range"
            min="1000"
            max="4000"
            value={target}
            onChange={(e) => setTarget(parseInt(e.target.value))}
          />
        </div>

        <ProgressBar current={consumed} target={target} />
      </CyberPanel>

      {/* 2. INTAKE MODULES */}
      <div className="intake-grid">
        {intakeUnits.map(unit => (
          <CyberPanel key={unit.id} title={unit.label} unit={unit.id}>
            <div className="item-table">
              {unit.items.length > 0 ? unit.items.map((item, i) => (
                <div key={i} className="item-row">
                  <span className="item-name">{item.name}</span>
                  <span className="item-kcal">{item.kcal} KCAL</span>
                </div>
              )) : (
                <div className="no-data">STATUS: EMPTY</div>
              )}
            </div>
            <div className="unit-actions">
              <TerminalButton label="[ + LOG ]" onClick={() => setDialogOpen(true)} />
            </div>
          </CyberPanel>
        ))}
      </div>

      {/* 4. FLOATING HUD BOTTOM DOCK */}
      <div className="bottom-dock">
        <div className="diet-selector">
          <select value={diet} onChange={(e) => setDiet(e.target.value)}>
            <option value="BALANCED">DIET // BALANCED</option>
            <option value="KETO">DIET // KETO</option>
            <option value="HIGH_PROTEIN">DIET // HIGH_PROTEIN</option>
          </select>
        </div>
        <TerminalButton
          label="// AI_REBALANCE"
          variant="pulsing"
          pulsing
          onClick={handleAiRebalance}
        />
      </div>

      {/* 3. TERMINAL DIALOG */}
      {isDialogOpen && (
        <div className="terminal-overlay">
          <div className="terminal-dialog">
            <div className="prompt">
              > ENTER INTAKE STRING... <span className="cursor">_</span>
            </div>
            <input
              autoFocus
              className="terminal-input"
              onKeyDown={handleLogSubmit}
            />
            <div className="dialog-actions">
              <TerminalButton label="ABORT" onClick={() => setDialogOpen(false)} variant="secondary" />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
