var ServerNetworkStarter = Java.type("com.github.nickid2018.chemistrylab.server.ServerNetworkStarter");
var InetAddress = Java.type("java.net.InetAddress");

/**
 * Get the "ServerStarter" instance
 */
function getServerStarterInstance() {
    server = ServerNetworkStarter.instance;
    if(server == null)
        throw new Error("Server Starter hasn't been constructed");
    return server;
}

/**
 * Force stop the server
 */
function forceStopServer() {
    server = getServerStarterInstance();
    if(!server.isActive())
        throw new Error("Server Starter hasn't been activated");
    server.stop();
}

/**
 *
 * @param address Address
 * @returns {*} InetAddress Object
 */
function getInetAddress(address){
    return InetAddress.getByName(address);
}

/**
 * Start TCP Server
 * @param inetAddress InetAddress for TCP Server, null-able
 * @param port Port for TCP Server
 * @param timeout Timeout for TCP Server
 */
function startTCPServer(inetAddress, port, timeout) {
    server = getServerStarterInstance();
    if(server.isActive())
        throw new Error("Server Starter has been activated");
    server.startTcpServer(inetAddress, port, timeout);
}

// --- API Invocation ---
/* ****** Initialization ****** */
function __serverInterface__init__() {
    logDebug("builtin package 'serverInterface' loaded");
}