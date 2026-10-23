import * as React from 'react';
import * as ReactDom from 'react-dom';
import { Version } from '@microsoft/sp-core-library';
import {
  type IPropertyPaneConfiguration,
  PropertyPaneToggle
} from '@microsoft/sp-property-pane';
import { BaseClientSideWebPart } from '@microsoft/sp-webpart-base';
import { SPComponentLoader } from '@microsoft/sp-loader';

import * as strings from 'VtlcHomeWebPartStrings';
import VtlcHome from './components/VtlcHome';
import { IVtlcHomeProps } from './components/IVtlcHomeProps';

// The design uses Inter; the stylesheet falls back to Segoe UI if this can't load.
const INTER_FONT_URL = 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap';

export interface IVtlcHomeWebPartProps {
  showSiteHeader: boolean;
}

export default class VtlcHomeWebPart extends BaseClientSideWebPart<IVtlcHomeWebPartProps> {

  public render(): void {
    const element: React.ReactElement<IVtlcHomeProps> = React.createElement(
      VtlcHome,
      {
        siteUrl: this.context.pageContext.web.absoluteUrl,
        showSiteHeader: this.properties.showSiteHeader !== false
      }
    );

    ReactDom.render(element, this.domElement);
  }

  protected onInit(): Promise<void> {
    SPComponentLoader.loadCss(INTER_FONT_URL);
    return super.onInit();
  }

  protected onDispose(): void {
    ReactDom.unmountComponentAtNode(this.domElement);
  }

  protected get dataVersion(): Version {
    return Version.parse('1.0');
  }

  protected getPropertyPaneConfiguration(): IPropertyPaneConfiguration {
    return {
      pages: [
        {
          header: {
            description: strings.PropertyPaneDescription
          },
          groups: [
            {
              groupName: strings.LayoutGroupName,
              groupFields: [
                PropertyPaneToggle('showSiteHeader', {
                  label: strings.ShowSiteHeaderFieldLabel,
                  onText: strings.ShowSiteHeaderOnText,
                  offText: strings.ShowSiteHeaderOffText
                })
              ]
            }
          ]
        }
      ]
    };
  }
}
