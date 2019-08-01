const cacheName = 'v1'

self.addEventListener('install', () => {
  console.log('Service worker installed')
})

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cache => {
          if (cache !== cacheName) {
            return caches.delete(cache)
          }
        })
      )
    })
  )
})

self.addEventListener('fetch', event => {
  event.respondWith(
    fetch(event.request)
    .then(response => {
      const responseClone = response.clone()
      caches.open(cacheName)
      .then(cashe => {
        cashe.put(event.request, responseClone)
      })
      return response
    })
    .catch(() => caches.match(event.request).then(response => response))
  )
})
