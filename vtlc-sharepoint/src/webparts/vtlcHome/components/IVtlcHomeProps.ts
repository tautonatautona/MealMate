export interface IVtlcHomeProps {
  /** Absolute URL of the current site, used to resolve the page links in content.ts. */
  siteUrl: string;
  /** Renders the VTLC branded header and navigation bar above the hero. */
  showSiteHeader: boolean;
}
