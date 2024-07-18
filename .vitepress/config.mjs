import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  base: '/stellaris/',
  title: "The Stellaris Project Documentation",
  description: "Learn how to create your own planets !",
  head: [['link', { rel: 'icon', href: '/favicon.ico' }]],
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Documention', link: '/custom-planets' },
      { text: 'Wiki', link: '/wiki/getting-started' },
      { text: 'Team', link: '/team' }

    ],

    sidebar: [
      {
        text: 'Documentation',
        items: [
          { text: 'The Planet file', link: '/custom-planets' },
          { text: 'The Screen files', link: '/custom-screen' }
        ]
      },
      {
        text: 'Wiki',
        items: [
          { text: 'Getting Started', link: '/wiki/getting-started' },
          { text: 'Fuel', link: '/wiki/fuel' },
          { text: 'Electricity', link: '/wiki/electricity' },
          { text: 'FAQ', link: '/wiki/faq' }

        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/st0x0ef/stellaris' },
      { icon: 'youtube', link: 'https://www.youtube.com/channel/UCzrr9q1Afqu-Yb9i7nn1uPA' },
      { icon: 'discord', link: 'https://discord.gg/project-stellaris-698598471896268931' }

    ],

    footer: {
      message: 'Made by the Stellaris team with 💖.'
    }

  },
  markdown: {
    math: true
  }
})
