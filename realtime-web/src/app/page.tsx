export default function Home() {
  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <header className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Realtime Monitoring Dashboard
          </h1>
          <p className="text-gray-600">
            Live system monitoring and performance metrics
          </p>
        </header>

        {/* Status Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div className="flex items-center">
              <div className="w-3 h-3 bg-green-500 rounded-full mr-3"></div>
              <span className="text-sm font-medium text-gray-600">
                System Status
              </span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-2">Online</p>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div className="flex items-center">
              <div className="w-3 h-3 bg-blue-500 rounded-full mr-3 animate-pulse"></div>
              <span className="text-sm font-medium text-gray-600">
                Active Users
              </span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-2">1,247</p>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div className="flex items-center">
              <div className="w-3 h-3 bg-yellow-500 rounded-full mr-3"></div>
              <span className="text-sm font-medium text-gray-600">
                CPU Usage
              </span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-2">67%</p>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div className="flex items-center">
              <div className="w-3 h-3 bg-purple-500 rounded-full mr-3"></div>
              <span className="text-sm font-medium text-gray-600">Memory</span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-2">4.2GB</p>
          </div>
        </div>

        {/* Main Monitoring Area */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Real-time Chart */}
          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              System Performance
            </h3>
            <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center">
              <div className="text-center">
                <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <svg
                    className="w-8 h-8 text-blue-600"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                    />
                  </svg>
                </div>
                <p className="text-gray-600">Real-time Chart</p>
                <p className="text-sm text-gray-500">
                  Live performance metrics
                </p>
              </div>
            </div>
          </div>

          {/* Live Events */}
          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">
              Live Events
            </h3>
            <div className="space-y-3">
              <div className="flex items-center p-3 bg-green-50 rounded-lg">
                <div className="w-2 h-2 bg-green-500 rounded-full mr-3"></div>
                <span className="text-sm text-gray-700">
                  User login successful
                </span>
                <span className="text-xs text-gray-500 ml-auto">2s ago</span>
              </div>
              <div className="flex items-center p-3 bg-blue-50 rounded-lg">
                <div className="w-2 h-2 bg-blue-500 rounded-full mr-3"></div>
                <span className="text-sm text-gray-700">
                  Database backup completed
                </span>
                <span className="text-xs text-gray-500 ml-auto">5s ago</span>
              </div>
              <div className="flex items-center p-3 bg-yellow-50 rounded-lg">
                <div className="w-2 h-2 bg-yellow-500 rounded-full mr-3"></div>
                <span className="text-sm text-gray-700">
                  High memory usage detected
                </span>
                <span className="text-xs text-gray-500 ml-auto">12s ago</span>
              </div>
              <div className="flex items-center p-3 bg-purple-50 rounded-lg">
                <div className="w-2 h-2 bg-purple-500 rounded-full mr-3"></div>
                <span className="text-sm text-gray-700">
                  New device connected
                </span>
                <span className="text-xs text-gray-500 ml-auto">18s ago</span>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <footer className="mt-12 text-center text-gray-500 text-sm">
          <p>
            Realtime Monitoring System • Last updated:{" "}
            {new Date().toLocaleTimeString()}
          </p>
        </footer>
      </div>
    </div>
  );
}
