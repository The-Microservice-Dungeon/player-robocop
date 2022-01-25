export const apiLink = (path, params) => {
    if (!path.startsWith('/')) path = '/' + path
    let urlString = window.location.protocol + window.location.host + path
    if (urlString.endsWith('/')) {
        urlString = urlString.substring(0, urlString.length - 1)
    }
    let url = new URL(urlString)
    if (params !== undefined && Object.keys(params).length > 0) {
        Object.keys(params).forEach(key => {
            if (params[key] !== undefined) {
                url.searchParams.append(key, params[key])
            }
        })
    }
    return url
}

export const apiCall = (method = 'GET', path, params = {}, body = undefined, headers = {}) => {
    const url = path instanceof URL ? path : apiLink(path, params)
    return fetch(url, {
        headers: {
            ...headers,
        },
        method,
        body,
    })
        .catch(console.error)
}

export const apiPost = (path, params, body, headers) => apiCall('POST', path, params, body, headers)
export const apiGet = (path, params, body, headers) => apiCall('GET', path, params, body, headers)

export const isScreenMobile = () => window.innerWidth <= 620
