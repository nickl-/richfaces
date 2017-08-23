extern LArc RiflemanTBL[];
class CRifleman : public LFsaAppl
{
public:
     int GetNumber();
     void SetNumber(int n);
     void SetLink(CRifleman *pFsaLeft,
         CRifleman
*pFsaRigtht);
     CRifleman *pFsaRightMan;
     CRifleman *pFsaLeftMan;
     CRifleman();
     CRifleman(int n, CWnd* pW, LArc
         *pTBL=RiflemanTBL);
     virtual ~CRifleman();
     bool operator==(const CRifleman
         &var) const;
     bool operator<(const CRifleman
         &var) const;
     bool operator!=(const CRifleman
         &var) const;
     bool operator>(const CRifleman
         &var) const;
protected:
  CWnd*   pParentWnd;
  CFireApp *pApp; //  
