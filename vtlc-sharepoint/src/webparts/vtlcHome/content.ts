// Page copy and link targets for the VTLC homepage (Figma: "HI-FI REFINED · Homepage").
// Link paths are relative to the site URL, so the web part works on any site it is added to.
// Update the page names below if the VTLC site pages are created with different names.

export type Accent = 'blue' | 'teal';

export interface ILink {
  label: string;
  path: string;
}

export interface IQuickAction {
  badge: string;
  title: string;
  description: string;
  path: string;
  accent: Accent;
}

export interface IFeaturedResource {
  title: string;
  meta: string;
  note: string;
  tag: string;
  path: string;
}

export interface IAsideItem {
  title: string;
  description: string;
  path: string;
}

export interface ISupportUnit {
  label: string;
  path: string;
  accent: Accent;
}

export const pages = {
  home: '/SitePages/VTLC.aspx',
  resources: '/SitePages/Resources-and-Training.aspx',
  training: '/SitePages/Training-and-Workshops.aspx',
  consultation: '/SitePages/Teaching-Consultation.aspx',
  onlineLearning: '/SitePages/Online-Teaching-and-LMS.aspx',
  curriculum: '/SitePages/Curriculum-and-QA.aspx',
  effectiveness: '/SitePages/Teaching-Effectiveness.aspx',
  research: '/SitePages/Research-and-Innovation.aspx',
  library: '/SitePages/Library-and-Information-Literacy.aspx',
  community: '/SitePages/Communities-of-Practice.aspx',
  ict: '/SitePages/ICT-Support-and-Service-Desk.aspx'
};

export const header = {
  logoText: 'BAC',
  title: 'Virtual Teaching & Learning Centre',
  subtitle: ['Botswana Accountancy College', 'Teaching Excellence'],
  secondaryAction: { label: 'Contact TLC', path: pages.consultation },
  primaryAction: { label: 'Get support', path: pages.ict }
};

export const navigation: ILink[] = [
  { label: 'Home', path: pages.home },
  { label: 'Resources', path: pages.resources },
  { label: 'Training', path: pages.training },
  { label: 'Online Learning', path: pages.onlineLearning },
  { label: 'Curriculum & QA', path: pages.curriculum },
  { label: 'Research', path: pages.research },
  { label: 'Community', path: pages.community },
  { label: 'ICT Help', path: pages.ict }
];

export const hero = {
  eyebrow: 'Welcome to VTLC',
  titleLines: ['Better teaching.', 'Stronger learning.'],
  lead: 'Find support, learning resources, workshops and practical teaching tools — in one place.',
  secondaryAction: { label: 'Explore services', path: '#vtlc-services' },
  primaryAction: { label: 'Browse resources', path: pages.resources },
  highlights: {
    eyebrow: 'Centre highlights',
    title: 'Teaching & learning',
    tagline: ['Connect', 'Learn', 'Innovate'],
    items: [
      { label: 'Inclusive practice', tone: 'teal' },
      { label: 'Assessment design', tone: 'blue' },
      { label: 'Digital pedagogy', tone: 'amber' }
    ] as { label: string; tone: 'teal' | 'blue' | 'amber' }[]
  }
};

export const quickActions = {
  title: 'What would you like to do?',
  description: "Quick access to the centre's most requested services.",
  linkLabel: 'Explore',
  items: [
    {
      badge: 'IR',
      title: 'Instructor resources',
      description: 'Pedagogy, assessment, classroom practice',
      path: pages.resources,
      accent: 'blue'
    },
    {
      badge: 'TR',
      title: 'Register for training',
      description: 'Workshops, webinars and professional learning',
      path: pages.training,
      accent: 'blue'
    },
    {
      badge: 'TC',
      title: 'Request consultation',
      description: 'Targeted advice from TLC support units',
      path: pages.consultation,
      accent: 'blue'
    },
    {
      badge: 'IT',
      title: 'ICT & platform support',
      description: 'Help with Moodle, Gradebook and teaching tools',
      path: pages.ict,
      accent: 'teal'
    }
  ] as IQuickAction[]
};

export const featuredResources = {
  title: 'Featured resources',
  description: 'Guidance for contemporary teaching and learning.',
  items: [
    {
      title: 'Designing inclusive learning experiences',
      meta: 'Guidance • Pedagogy',
      note: 'Available in the VTLC resource library',
      tag: 'Teaching resource',
      path: pages.resources
    },
    {
      title: 'Assessment design and feedback strategies',
      meta: 'Toolkit • Assessment & evaluation',
      note: 'Available in the VTLC resource library',
      tag: 'Downloadable guide',
      path: pages.curriculum
    },
    {
      title: 'Facilitating synchronous and asynchronous classes',
      meta: 'Guide • Online learning',
      note: 'Available in the VTLC resource library',
      tag: 'Digital teaching',
      path: pages.onlineLearning
    }
  ] as IFeaturedResource[]
};

export const learningAside = {
  title: 'Learning & development',
  items: [
    { title: 'Upcoming learning', description: 'Browse instructor workshops', path: pages.training },
    { title: 'Request training', description: 'Suggest a professional learning topic', path: pages.training },
    { title: 'Peer support', description: 'Connect with colleagues', path: pages.community }
  ] as IAsideItem[],
  action: { label: 'View all workshops', path: pages.training }
};

export const supportUnits = {
  title: 'Explore our support units',
  description: 'Services delivered in collaboration across BAC.',
  items: [
    { label: 'Academic Schools', path: pages.consultation, accent: 'blue' },
    { label: 'Online Learning', path: pages.onlineLearning, accent: 'teal' },
    { label: 'Library Services', path: pages.library, accent: 'blue' },
    { label: 'ICT', path: pages.ict, accent: 'teal' },
    { label: 'Research Office', path: pages.research, accent: 'blue' },
    { label: 'Curriculum Development', path: pages.curriculum, accent: 'teal' },
    { label: 'TL Technology', path: pages.effectiveness, accent: 'blue' }
  ] as ISupportUnit[]
};

export const footer = {
  breadcrumb: ['BAC', 'Virtual Teaching & Learning Centre'],
  links: [
    { label: 'Instructor support', path: pages.resources },
    { label: 'Online learning', path: pages.onlineLearning },
    { label: 'Research', path: pages.research },
    { label: 'Curriculum', path: pages.curriculum },
    { label: 'Library', path: pages.library },
    { label: 'ICT', path: pages.ict }
  ] as ILink[]
};
