import React, { useState, useEffect, useRef } from 'react';
import { LineChart, Line, BarChart, Bar, AreaChart, Area, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Radar, ScatterChart, Scatter } from 'recharts';
import { TrendingUp, Activity, Zap, Brain, Target, Globe, ChevronRight, Sparkles, Cpu, Database } from 'lucide-react';
import * as THREE from 'three';

const AdvancedDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [metrics, setMetrics] = useState({
    performance: 87,
    efficiency: 92,
    aiAccuracy: 95,
    throughput: 1247
  });
  const [realTimeData, setRealTimeData] = useState([]);
  const [prediction, setPrediction] = useState(null);
  const canvasRef = useRef(null);
  const sceneRef = useRef(null);

  // Generate sophisticated data
  const generateTimeSeriesData = () => {
    return Array.from({ length: 30 }, (_, i) => ({
      time: `T${i}`,
      neural: Math.sin(i * 0.3) * 30 + 70 + Math.random() * 10,
      quantum: Math.cos(i * 0.4) * 25 + 65 + Math.random() * 10,
      hybrid: Math.sin(i * 0.2) * 20 + 80 + Math.random() * 8,
      traditional: 50 + Math.random() * 20
    }));
  };

  const performanceData = generateTimeSeriesData();

  const algorithmComparison = [
    { algorithm: 'Neural Net', accuracy: 95.4, speed: 87, efficiency: 91 },
    { algorithm: 'Quantum', accuracy: 89.2, speed: 95, efficiency: 88 },
    { algorithm: 'Hybrid', accuracy: 97.1, speed: 82, efficiency: 94 },
    { algorithm: 'Classical', accuracy: 78.5, speed: 91, efficiency: 85 },
    { algorithm: 'Ensemble', accuracy: 96.8, speed: 79, efficiency: 92 }
  ];

  const distributionData = [
    { name: 'Neural Processing', value: 35, color: '#8b5cf6' },
    { name: 'Data Pipeline', value: 25, color: '#06b6d4' },
    { name: 'Model Training', value: 20, color: '#f59e0b' },
    { name: 'Inference', value: 15, color: '#10b981' },
    { name: 'Optimization', value: 5, color: '#ef4444' }
  ];

  const clusterData = Array.from({ length: 50 }, () => ({
    x: Math.random() * 100,
    y: Math.random() * 100,
    z: Math.random() * 50 + 50
  }));

  // Initialize 3D Scene
  useEffect(() => {
    if (!canvasRef.current || sceneRef.current) return;

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, 400 / 300, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ 
      canvas: canvasRef.current, 
      alpha: true,
      antialias: true 
    });
    
    renderer.setSize(400, 300);
    renderer.setClearColor(0x000000, 0);
    
    // Create particle system
    const geometry = new THREE.BufferGeometry();
    const vertices = [];
    const colors = [];
    
    for (let i = 0; i < 1000; i++) {
      vertices.push(
        Math.random() * 20 - 10,
        Math.random() * 20 - 10,
        Math.random() * 20 - 10
      );
      colors.push(
        Math.random() * 0.5 + 0.5,
        Math.random() * 0.3 + 0.4,
        Math.random() * 0.5 + 0.5
      );
    }
    
    geometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
    geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3));
    
    const material = new THREE.PointsMaterial({ 
      size: 0.15, 
      vertexColors: true,
      transparent: true,
      opacity: 0.8
    });
    
    const particles = new THREE.Points(geometry, material);
    scene.add(particles);
    
    // Add connecting lines
    const lineGeometry = new THREE.BufferGeometry();
    const lineMaterial = new THREE.LineBasicMaterial({ 
      color: 0x8b5cf6, 
      transparent: true, 
      opacity: 0.3 
    });
    
    camera.position.z = 15;
    
    sceneRef.current = { scene, camera, renderer, particles };
    
    let frame = 0;
    const animate = () => {
      requestAnimationFrame(animate);
      frame += 0.01;
      
      particles.rotation.x = Math.sin(frame * 0.5) * 0.2;
      particles.rotation.y = frame * 0.3;
      
      const positions = geometry.attributes.position.array;
      for (let i = 0; i < positions.length; i += 3) {
        positions[i + 1] += Math.sin(frame + i) * 0.01;
      }
      geometry.attributes.position.needsUpdate = true;
      
      renderer.render(scene, camera);
    };
    
    animate();
    
    return () => {
      renderer.dispose();
      geometry.dispose();
      material.dispose();
    };
  }, []);

  // Real-time data simulation
  useEffect(() => {
    const interval = setInterval(() => {
      setRealTimeData(prev => {
        const newData = {
          time: new Date().toLocaleTimeString(),
          value: Math.random() * 100,
          anomaly: Math.random() > 0.9
        };
        return [...prev.slice(-20), newData];
      });
      
      setMetrics(prev => ({
        performance: Math.min(100, Math.max(0, prev.performance + (Math.random() - 0.5) * 5)),
        efficiency: Math.min(100, Math.max(0, prev.efficiency + (Math.random() - 0.5) * 3)),
        aiAccuracy: Math.min(100, Math.max(0, prev.aiAccuracy + (Math.random() - 0.5) * 2)),
        throughput: Math.floor(Math.max(0, prev.throughput + (Math.random() - 0.5) * 100))
      }));
    }, 2000);

    return () => clearInterval(interval);
  }, []);

  // AI Prediction Engine
  const runPrediction = () => {
    const trend = performanceData.slice(-10).reduce((acc, d) => acc + d.neural, 0) / 10;
    const volatility = Math.random() * 10;
    const prediction = {
      value: trend + (Math.random() - 0.5) * 20,
      confidence: 85 + Math.random() * 10,
      trend: trend > 70 ? 'Positive' : 'Negative',
      factors: ['Neural density increase', 'Quantum coherence stable', 'Optimization converged']
    };
    setPrediction(prediction);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-purple-950 to-slate-950 text-white p-6">
      {/* Animated background */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none opacity-20">
        <div className="absolute w-96 h-96 bg-purple-500 rounded-full blur-3xl -top-48 -left-48 animate-pulse"></div>
        <div className="absolute w-96 h-96 bg-cyan-500 rounded-full blur-3xl -bottom-48 -right-48 animate-pulse" style={{animationDelay: '1s'}}></div>
      </div>

      <div className="relative z-10 max-w-7xl mx-auto">
        {/* Header */}
        <header className="mb-8">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 via-pink-400 to-cyan-400 bg-clip-text text-transparent">
                Quantum Neural Intelligence
              </h1>
              <p className="text-slate-400 mt-2">Advanced AI-Powered Analytics Platform</p>
            </div>
            <div className="flex gap-4">
              <div className="px-4 py-2 bg-green-500/20 border border-green-500/50 rounded-lg">
                <span className="text-green-400 font-semibold">System Operational</span>
              </div>
            </div>
          </div>

          {/* Metrics Bar */}
          <div className="grid grid-cols-4 gap-4">
            {[
              { icon: Activity, label: 'Performance', value: metrics.performance, color: 'purple' },
              { icon: Zap, label: 'Efficiency', value: metrics.efficiency, color: 'cyan' },
              { icon: Brain, label: 'AI Accuracy', value: metrics.aiAccuracy, color: 'pink' },
              { icon: Target, label: 'Throughput', value: metrics.throughput, color: 'green' }
            ].map((metric, i) => (
              <div key={i} className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-4 hover:border-purple-500/50 transition-all">
                <div className="flex items-center gap-3 mb-2">
                  <metric.icon className={`text-${metric.color}-400`} size={20} />
                  <span className="text-slate-400 text-sm">{metric.label}</span>
                </div>
                <div className="text-2xl font-bold">
                  {typeof metric.value === 'number' && metric.value < 200 
                    ? `${metric.value.toFixed(1)}%` 
                    : metric.value}
                </div>
              </div>
            ))}
          </div>
        </header>

        {/* Navigation Tabs */}
        <div className="flex gap-2 mb-6">
          {['overview', 'analytics', 'predictions', '3d-viz'].map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-6 py-3 rounded-lg font-semibold transition-all ${
                activeTab === tab
                  ? 'bg-purple-600 text-white shadow-lg shadow-purple-500/50'
                  : 'bg-slate-900/50 text-slate-400 hover:bg-slate-800'
              }`}
            >
              {tab.split('-').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ')}
            </button>
          ))}
        </div>

        {/* Content Area */}
        {activeTab === 'overview' && (
          <div className="grid grid-cols-2 gap-6">
            <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
                <TrendingUp className="text-purple-400" />
                Multi-Modal Performance
              </h3>
              <ResponsiveContainer width="100%" height={300}>
                <AreaChart data={performanceData}>
                  <defs>
                    <linearGradient id="neural" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.8}/>
                      <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0}/>
                    </linearGradient>
                    <linearGradient id="quantum" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.8}/>
                      <stop offset="95%" stopColor="#06b6d4" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                  <XAxis dataKey="time" stroke="#64748b" />
                  <YAxis stroke="#64748b" />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                    labelStyle={{ color: '#94a3b8' }}
                  />
                  <Legend />
                  <Area type="monotone" dataKey="neural" stroke="#8b5cf6" fillOpacity={1} fill="url(#neural)" />
                  <Area type="monotone" dataKey="quantum" stroke="#06b6d4" fillOpacity={1} fill="url(#quantum)" />
                  <Area type="monotone" dataKey="hybrid" stroke="#f59e0b" fillOpacity={0.3} fill="#f59e0b" />
                </AreaChart>
              </ResponsiveContainer>
            </div>

            <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
                <Cpu className="text-cyan-400" />
                Resource Distribution
              </h3>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={distributionData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={100}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {distributionData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                  />
                </PieChart>
              </ResponsiveContainer>
              <div className="mt-4 space-y-2">
                {distributionData.map((item, i) => (
                  <div key={i} className="flex items-center justify-between text-sm">
                    <div className="flex items-center gap-2">
                      <div className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }}></div>
                      <span className="text-slate-300">{item.name}</span>
                    </div>
                    <span className="text-slate-400">{item.value}%</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="col-span-2 bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
                <Database className="text-pink-400" />
                Algorithm Benchmark Comparison
              </h3>
              <ResponsiveContainer width="100%" height={300}>
                <RadarChart data={algorithmComparison}>
                  <PolarGrid stroke="#334155" />
                  <PolarAngleAxis dataKey="algorithm" stroke="#94a3b8" />
                  <PolarRadiusAxis stroke="#64748b" />
                  <Radar name="Accuracy" dataKey="accuracy" stroke="#8b5cf6" fill="#8b5cf6" fillOpacity={0.3} />
                  <Radar name="Speed" dataKey="speed" stroke="#06b6d4" fill="#06b6d4" fillOpacity={0.3} />
                  <Radar name="Efficiency" dataKey="efficiency" stroke="#10b981" fill="#10b981" fillOpacity={0.3} />
                  <Legend />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                  />
                </RadarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {activeTab === 'analytics' && (
          <div className="grid grid-cols-2 gap-6">
            <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4">Real-Time Anomaly Detection</h3>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={realTimeData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                  <XAxis dataKey="time" stroke="#64748b" />
                  <YAxis stroke="#64748b" />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                  />
                  <Line type="monotone" dataKey="value" stroke="#f59e0b" strokeWidth={2} dot={{ fill: '#f59e0b', r: 4 }} />
                </LineChart>
              </ResponsiveContainer>
              <div className="mt-4 text-sm text-slate-400">
                Monitoring {realTimeData.length} data points • {realTimeData.filter(d => d.anomaly).length} anomalies detected
              </div>
            </div>

            <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4">Feature Correlation Matrix</h3>
              <ResponsiveContainer width="100%" height={300}>
                <ScatterChart>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                  <XAxis dataKey="x" stroke="#64748b" />
                  <YAxis dataKey="y" stroke="#64748b" />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                    cursor={{ strokeDasharray: '3 3' }}
                  />
                  <Scatter name="Clusters" data={clusterData} fill="#8b5cf6" />
                </ScatterChart>
              </ResponsiveContainer>
            </div>

            <div className="col-span-2 bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4">Comparative Algorithm Performance</h3>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={algorithmComparison}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                  <XAxis dataKey="algorithm" stroke="#64748b" />
                  <YAxis stroke="#64748b" />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                  />
                  <Legend />
                  <Bar dataKey="accuracy" fill="#8b5cf6" />
                  <Bar dataKey="speed" fill="#06b6d4" />
                  <Bar dataKey="efficiency" fill="#10b981" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {activeTab === 'predictions' && (
          <div className="grid grid-cols-2 gap-6">
            <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
                <Brain className="text-purple-400" />
                AI Prediction Engine
              </h3>
              <button
                onClick={runPrediction}
                className="w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 px-6 py-3 rounded-lg font-semibold mb-6 transition-all transform hover:scale-105 flex items-center justify-center gap-2"
              >
                <Sparkles size={20} />
                Generate Prediction
              </button>
              
              {prediction && (
                <div className="space-y-4">
                  <div className="bg-slate-800/50 rounded-lg p-4">
                    <div className="text-sm text-slate-400 mb-1">Predicted Value</div>
                    <div className="text-3xl font-bold text-purple-400">{prediction.value.toFixed(2)}</div>
                  </div>
                  
                  <div className="bg-slate-800/50 rounded-lg p-4">
                    <div className="text-sm text-slate-400 mb-1">Confidence</div>
                    <div className="flex items-center gap-3">
                      <div className="flex-1 bg-slate-700 rounded-full h-2">
                        <div 
                          className="bg-gradient-to-r from-green-500 to-emerald-400 h-2 rounded-full transition-all duration-1000"
                          style={{ width: `${prediction.confidence}%` }}
                        ></div>
                      </div>
                      <span className="text-emerald-400 font-semibold">{prediction.confidence.toFixed(1)}%</span>
                    </div>
                  </div>
                  
                  <div className="bg-slate-800/50 rounded-lg p-4">
                    <div className="text-sm text-slate-400 mb-2">Key Factors</div>
                    <div className="space-y-2">
                      {prediction.factors.map((factor, i) => (
                        <div key={i} className="flex items-center gap-2 text-sm">
                          <ChevronRight className="text-cyan-400" size={16} />
                          <span className="text-slate-300">{factor}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                  
                  <div className={`rounded-lg p-4 ${
                    prediction.trend === 'Positive' 
                      ? 'bg-green-500/20 border border-green-500/50' 
                      : 'bg-red-500/20 border border-red-500/50'
                  }`}>
                    <div className="text-sm mb-1">Trend Analysis</div>
                    <div className={`text-xl font-bold ${
                      prediction.trend === 'Positive' ? 'text-green-400' : 'text-red-400'
                    }`}>
                      {prediction.trend} Trajectory
                    </div>
                  </div>
                </div>
              )}
            </div>

            <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
              <h3 className="text-xl font-bold mb-4">Forecast Visualization</h3>
              <ResponsiveContainer width="100%" height={400}>
                <LineChart data={[...performanceData, 
                  { time: 'F1', neural: prediction?.value || 0, forecast: true },
                  { time: 'F2', neural: (prediction?.value || 0) + 5, forecast: true },
                  { time: 'F3', neural: (prediction?.value || 0) + 8, forecast: true }
                ]}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                  <XAxis dataKey="time" stroke="#64748b" />
                  <YAxis stroke="#64748b" />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155' }}
                  />
                  <Legend />
                  <Line type="monotone" dataKey="neural" stroke="#8b5cf6" strokeWidth={2} dot={{ r: 3 }} />
                  <Line type="monotone" dataKey="quantum" stroke="#06b6d4" strokeWidth={2} dot={{ r: 3 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {activeTab === '3d-viz' && (
          <div className="bg-slate-900/50 backdrop-blur border border-slate-800 rounded-xl p-6">
            <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
              <Globe className="text-cyan-400" />
              3D Neural Network Visualization
            </h3>
            <div className="flex justify-center">
              <canvas ref={canvasRef} className="rounded-lg border border-slate-700" />
            </div>
            <div className="mt-6 grid grid-cols-3 gap-4">
              <div className="bg-slate-800/50 rounded-lg p-4 text-center">
                <div className="text-2xl font-bold text-purple-400">1,000</div>
                <div className="text-sm text-slate-400 mt-1">Active Nodes</div>
              </div>
              <div className="bg-slate-800/50 rounded-lg p-4 text-center">
                <div className="text-2xl font-bold text-cyan-400">47.2ms</div>
                <div className="text-sm text-slate-400 mt-1">Avg Latency</div>
              </div>
              <div className="bg-slate-800/50 rounded-lg p-4 text-center">
                <div className="text-2xl font-bold text-pink-400">98.7%</div>
                <div className="text-sm text-slate-400 mt-1">Uptime</div>
              </div>
            </div>
          </div>
        )}

        {/* Footer */}
        <footer className="mt-8 text-center text-slate-500 text-sm">
          <p>Powered by Quantum Neural Intelligence • Real-time Analytics Engine v4.2</p>
        </footer>
      </div>
    </div>
  );
};

export default AdvancedDashboard;