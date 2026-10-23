import * as React from 'react';
import styles from './VtlcHome.module.scss';
import type { IVtlcHomeProps } from './IVtlcHomeProps';
import {
  type Accent,
  featuredResources,
  footer,
  header,
  hero,
  learningAside,
  navigation,
  quickActions,
  supportUnits
} from '../content';

const accentClass: Record<Accent, string> = {
  blue: styles.accentBlue,
  teal: styles.accentTeal
};

const toneClass: Record<'teal' | 'blue' | 'amber', string> = {
  teal: styles.dotTeal,
  blue: styles.dotBlue,
  amber: styles.dotAmber
};

export default class VtlcHome extends React.Component<IVtlcHomeProps> {
  public render(): React.ReactElement<IVtlcHomeProps> {
    const { showSiteHeader } = this.props;

    return (
      <div className={styles.vtlcHome}>
        {showSiteHeader && this._renderSiteHeader()}
        <div className={styles.page}>
          {this._renderHero()}
          {this._renderQuickActions()}
          {this._renderFeatured()}
          {this._renderSupportUnits()}
          {this._renderFooter()}
        </div>
      </div>
    );
  }

  private _href(path: string): string {
    return path.charAt(0) === '#' ? path : `${this.props.siteUrl}${path}`;
  }

  private _renderSiteHeader(): React.ReactElement {
    return (
      <header className={styles.siteHeader}>
        <div className={styles.brandBar}>
          <a className={styles.brand} href={this._href(navigation[0].path)}>
            <span className={styles.logo} aria-hidden="true">{header.logoText}</span>
            <span className={styles.brandText}>
              <span className={styles.siteTitle}>{header.title}</span>
              <span className={styles.siteSubtitle}>{header.subtitle.join('   •   ')}</span>
            </span>
          </a>
          <div className={styles.headerActions}>
            <a className={`${styles.button} ${styles.buttonSecondary}`} href={this._href(header.secondaryAction.path)}>
              {header.secondaryAction.label}
            </a>
            <a className={`${styles.button} ${styles.buttonPrimary}`} href={this._href(header.primaryAction.path)}>
              {header.primaryAction.label}<span aria-hidden="true">  →</span>
            </a>
          </div>
        </div>
        <nav className={styles.nav} aria-label="VTLC">
          <ul>
            {navigation.map(link => (
              <li key={link.label}>
                <a href={this._href(link.path)}>{link.label}</a>
              </li>
            ))}
          </ul>
        </nav>
      </header>
    );
  }

  private _renderHero(): React.ReactElement {
    const { highlights } = hero;

    return (
      <section className={styles.hero}>
        <span className={styles.heroRingOuter} aria-hidden="true" />
        <span className={styles.heroRingInner} aria-hidden="true" />
        <div className={styles.heroContent}>
          <span className={styles.heroEyebrow}>{hero.eyebrow}</span>
          <h1 className={styles.heroTitle}>
            {hero.titleLines.map(line => <span key={line}>{line}</span>)}
          </h1>
          <p className={styles.heroLead}>{hero.lead}</p>
          <div className={styles.heroActions}>
            <a className={`${styles.button} ${styles.buttonSecondary} ${styles.buttonWide}`} href={this._href(hero.secondaryAction.path)}>
              {hero.secondaryAction.label}<span aria-hidden="true">  →</span>
            </a>
            <a className={`${styles.button} ${styles.buttonPrimary} ${styles.buttonWide}`} href={this._href(hero.primaryAction.path)}>
              {hero.primaryAction.label}
            </a>
          </div>
        </div>
        <aside className={styles.highlights} aria-label={highlights.eyebrow}>
          <span className={styles.pill}>{highlights.eyebrow}</span>
          <h2 className={styles.highlightsTitle}>{highlights.title}</h2>
          <p className={styles.highlightsTagline}>{highlights.tagline.join(' • ')}</p>
          <hr className={styles.divider} />
          <ul className={styles.highlightsList}>
            {highlights.items.map(item => (
              <li key={item.label}>
                <span className={`${styles.dot} ${toneClass[item.tone]}`} aria-hidden="true" />
                {item.label}
              </li>
            ))}
          </ul>
        </aside>
      </section>
    );
  }

  private _renderSectionHeading(title: string, description: string, id?: string): React.ReactElement {
    return (
      <div className={styles.sectionHeading}>
        <h2 id={id}>{title}</h2>
        <p>{description}</p>
      </div>
    );
  }

  private _renderQuickActions(): React.ReactElement {
    return (
      <section className={styles.section} aria-labelledby="vtlc-services">
        {this._renderSectionHeading(quickActions.title, quickActions.description, 'vtlc-services')}
        <ul className={styles.quickGrid}>
          {quickActions.items.map(item => (
            <li key={item.title}>
              <a className={`${styles.card} ${styles.quickCard} ${accentClass[item.accent]}`} href={this._href(item.path)}>
                <span className={styles.iconTile} aria-hidden="true">{item.badge}</span>
                <span className={styles.quickTitle}>{item.title}</span>
                <span className={styles.quickDescription}>{item.description}</span>
                <span className={styles.quickLink}>{quickActions.linkLabel}<span aria-hidden="true">   →</span></span>
              </a>
            </li>
          ))}
        </ul>
      </section>
    );
  }

  private _renderFeatured(): React.ReactElement {
    return (
      <section className={styles.section} aria-labelledby="vtlc-featured">
        {this._renderSectionHeading(featuredResources.title, featuredResources.description, 'vtlc-featured')}
        <div className={styles.featuredLayout}>
          <ul className={styles.resourceList}>
            {featuredResources.items.map(item => (
              <li key={item.title}>
                <a className={`${styles.card} ${styles.resource}`} href={this._href(item.path)}>
                  <span className={`${styles.iconTile} ${styles.accentBlue}`} aria-hidden="true">↗</span>
                  <span className={styles.resourceBody}>
                    <span className={styles.resourceTitle}>{item.title}</span>
                    <span className={styles.resourceMeta}>{item.meta}</span>
                    <span className={styles.resourceNote}>{item.note}</span>
                  </span>
                  <span className={styles.tag}>{item.tag}</span>
                </a>
              </li>
            ))}
          </ul>
          <aside className={`${styles.card} ${styles.aside}`} aria-labelledby="vtlc-learning">
            <h3 id="vtlc-learning" className={styles.asideTitle}>{learningAside.title}</h3>
            <hr className={styles.divider} />
            <ul className={styles.asideList}>
              {learningAside.items.map(item => (
                <li key={item.title}>
                  <a href={this._href(item.path)}>
                    <span className={styles.asideItemTitle}>{item.title}</span>
                    <span className={styles.asideItemDescription}>{item.description}</span>
                  </a>
                </li>
              ))}
            </ul>
            <a className={`${styles.button} ${styles.buttonPrimary} ${styles.asideButton}`} href={this._href(learningAside.action.path)}>
              {learningAside.action.label}<span aria-hidden="true">   →</span>
            </a>
          </aside>
        </div>
      </section>
    );
  }

  private _renderSupportUnits(): React.ReactElement {
    return (
      <section className={`${styles.section} ${styles.unitsSection}`} aria-labelledby="vtlc-units">
        {this._renderSectionHeading(supportUnits.title, supportUnits.description, 'vtlc-units')}
        <ul className={styles.unitGrid}>
          {supportUnits.items.map(unit => (
            <li key={unit.label}>
              <a className={styles.unit} href={this._href(unit.path)}>
                <span className={`${styles.unitDot} ${accentClass[unit.accent]}`} aria-hidden="true" />
                {unit.label}
              </a>
            </li>
          ))}
        </ul>
      </section>
    );
  }

  private _renderFooter(): React.ReactElement {
    return (
      <footer className={styles.footer}>
        <p className={styles.footerTitle}>{footer.breadcrumb.join('   /   ')}</p>
        <ul className={styles.footerLinks}>
          {footer.links.map(link => (
            <li key={link.label}>
              <a href={this._href(link.path)}>{link.label}</a>
            </li>
          ))}
        </ul>
      </footer>
    );
  }
}
