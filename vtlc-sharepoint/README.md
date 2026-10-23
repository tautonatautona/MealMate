# BAC VTLC Homepage web part

SharePoint Framework (SPFx 1.23) web part that renders the **BAC VTLC Hi-Fi "01 — Homepage"** design
(Figma file `jQEhPR8zExrDww8owd4hWw`, frame `24:2`) for
`https://mybac.sharepoint.com/sites/VTLC_5ced37/SitePages/VTLC.aspx`.

## Build

Requires Node 22.14+ (< 23).

```bash
npm install
npm run build        # lint, compile, and package sharepoint/solution/vtlc-sharepoint.sppkg
npm run start        # local workbench for development
```

## Deploy to VTLC.aspx

1. Upload `sharepoint/solution/vtlc-sharepoint.sppkg` to the tenant (or site) App Catalog and choose **Deploy**.
   The solution uses `skipFeatureDeployment`, so it is available on all sites straight away.
2. Open `VTLC.aspx`, choose **Edit**, and add a **Full-width section**.
3. Add the **VTLC Homepage** web part (Advanced group) to that section and **Republish**.

The web part draws its own VTLC header and navigation by default. To use the native SharePoint site header
instead, turn **VTLC header and navigation** off in the web part's property pane.

## Editing content

All copy and link targets are in `src/webparts/vtlcHome/content.ts`. Link paths are relative to the site, e.g.
`/SitePages/Resources-and-Training.aspx`. Rename them there if the VTLC sub-pages use different names.
